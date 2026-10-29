package pe.edu.upeu.autoandes.service.impl;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.*;
import pe.edu.upeu.autoandes.exception.ApiException;
import pe.edu.upeu.autoandes.mapper.AutoAndesMapper;
import pe.edu.upeu.autoandes.repository.*;
import pe.edu.upeu.autoandes.service.service.OrdenService;

@Service
@Transactional(readOnly = true)
public class OrdenServiceImpl implements OrdenService {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(OrdenServiceImpl.class);
  private final OrdenServicioRepository ordenes;
  private final VehiculoRepository vehiculos;
  private final ServicioRepository servicios;
  private final Clock clock;

  public OrdenServiceImpl(
      OrdenServicioRepository o, VehiculoRepository v, ServicioRepository s, Clock c) {
    ordenes = o;
    vehiculos = v;
    servicios = s;
    clock = c;
  }

  @Transactional
  public OrdenResponseDTO registrar(OrdenRequestDTO d, String username) {
    if (d.detalles() == null || d.detalles().isEmpty() || d.detalles().size() > 10)
      throw new ApiException(400, "La orden requiere entre 1 y 10 detalles");
    Set<Long> ids = new HashSet<>();
    for (DetalleRequestDTO item : d.detalles()) {
      if (item == null
          || item.servicioId() == null
          || item.cantidad() == null
          || item.cantidad() < 1
          || item.cantidad() > 5) throw new ApiException(400, "Cantidad permitida: 1 a 5");
      if (!ids.add(item.servicioId()))
        throw new ApiException(400, "No se permite repetir un servicio");
    }
    // Serializa los registros del mismo vehículo para validar contra la última orden confirmada.
    Vehiculo v =
        vehiculos.bloquear(d.vehiculoId()).orElseThrow(() -> ApiException.noExiste("Vehículo"));
    ordenes
        .findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(v.getId(), EstadoOrden.REGISTRADA)
        .ifPresent(
            ultima -> {
              if (d.kilometraje() < ultima.getKilometraje())
                throw new ApiException(
                    409, "El último kilometraje registrado es " + ultima.getKilometraje());
            });
    OrdenServicio o = new OrdenServicio();
    o.setVehiculo(v);
    o.setKilometraje(d.kilometraje());
    o.setFecha(LocalDateTime.now(clock));
    o.setEstado(EstadoOrden.REGISTRADA);
    o.setUsuarioRegistro(username);
    o.setTotal(BigDecimal.ZERO);
    long numero = ordenes.siguienteNumero();
    if (numero > 999999) throw new ApiException(409, "Se agotó la numeración OS de seis dígitos");
    o.setNumero(String.format("OS-%06d", numero));
    for (DetalleRequestDTO item : d.detalles()) {
      Servicio s =
          servicios
              .findById(item.servicioId())
              .orElseThrow(() -> ApiException.noExiste("Servicio"));
      if (!Boolean.TRUE.equals(s.getActivo()))
        throw new ApiException(409, "El servicio " + s.getCodigo() + " está inactivo");
      DetalleOrden det = new DetalleOrden();
      det.setOrden(o);
      det.setServicio(s);
      det.setCantidad(item.cantidad());
      det.setPrecioUnitario(s.getPrecio());
      det.setSubtotal(s.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
      o.getDetalles().add(det);
      o.setTotal(o.getTotal().add(det.getSubtotal()));
    }
    o = ordenes.saveAndFlush(o);
    log.info("Orden registrada numero={} total={}", o.getNumero(), o.getTotal());
    return AutoAndesMapper.orden(o);
  }

  @Transactional
  public OrdenResponseDTO anular(Long id, AnularRequestDTO d) {
    if (d.motivo() == null || d.motivo().trim().length() < 10 || d.motivo().length() > 200)
      throw new ApiException(400, "El motivo debe tener entre 10 y 200 caracteres");
    OrdenServicio o = ordenes.bloquear(id).orElseThrow(() -> ApiException.noExiste("Orden"));
    if (o.getEstado() != EstadoOrden.REGISTRADA)
      throw new ApiException(409, "Solo se anulan órdenes REGISTRADAS");
    if (!o.getFecha().toLocalDate().equals(LocalDate.now(clock)))
      throw new ApiException(409, "Solo se puede anular el mismo día del registro");
    o.setEstado(EstadoOrden.ANULADA);
    o.setMotivoAnulacion(d.motivo().trim());
    log.info("Orden anulada numero={}", o.getNumero());
    return AutoAndesMapper.orden(o);
  }

  public OrdenResponseDTO obtener(Long id) {
    return AutoAndesMapper.orden(
        ordenes.findById(id).orElseThrow(() -> ApiException.noExiste("Orden")));
  }

  private void rango(LocalDate d, LocalDate h) {
    if (d != null && h != null && d.isAfter(h))
      throw new ApiException(400, "Desde no puede ser posterior a hasta");
  }

  public List<OrdenResponseDTO> buscar(
      String placa, EstadoOrden estado, LocalDate desde, LocalDate hasta) {
    rango(desde, hasta);
    return ordenes
        .buscar(
            placa == null || placa.isBlank() ? null : placa.trim(),
            estado,
            desde == null ? null : desde.atStartOfDay(),
            hasta == null ? null : hasta.plusDays(1).atStartOfDay())
        .stream()
        .map(AutoAndesMapper::orden)
        .toList();
  }

  public List<IngresoResponseDTO> ingresos(LocalDate desde, LocalDate hasta) {
    rango(desde, hasta);
    return ordenes.ingresos(
        desde == null ? null : desde.atStartOfDay(),
        hasta == null ? null : hasta.plusDays(1).atStartOfDay());
  }
}
