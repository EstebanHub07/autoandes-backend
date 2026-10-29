package pe.edu.upeu.autoandes.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.*;
import pe.edu.upeu.autoandes.exception.ApiException;
import pe.edu.upeu.autoandes.mapper.AutoAndesMapper;
import pe.edu.upeu.autoandes.repository.*;
import pe.edu.upeu.autoandes.service.service.ServicioService;

@Service
@Transactional(readOnly = true)
public class ServicioServiceImpl implements ServicioService {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ServicioServiceImpl.class);
  private final ServicioRepository servicios;
  private final DetalleOrdenRepository detalles;

  public ServicioServiceImpl(ServicioRepository s, DetalleOrdenRepository d) {
    servicios = s;
    detalles = d;
  }

  public List<ServicioResponseDTO> readAll() {
    return servicios.findAll().stream().map(AutoAndesMapper::servicio).toList();
  }

  private Servicio entidad(Long id) {
    return servicios.findById(id).orElseThrow(() -> ApiException.noExiste("Servicio"));
  }

  public ServicioResponseDTO read(Long id) {
    return AutoAndesMapper.servicio(entidad(id));
  }

  @Transactional
  public ServicioResponseDTO create(ServicioRequestDTO d) {
    return guardar(null, d);
  }

  @Transactional
  public ServicioResponseDTO update(Long id, ServicioRequestDTO d) {
    return guardar(id, d);
  }

  private ServicioResponseDTO guardar(Long id, ServicioRequestDTO d) {
    Servicio s = id == null ? new Servicio() : entidad(id);
    if (servicios.existsByCodigoAndIdNot(d.getCodigo(), id == null ? -1L : id))
      throw new ApiException(409, "El código de servicio ya existe");
    s.setCodigo(d.getCodigo());
    s.setNombre(d.getNombre());
    s.setPrecio(d.getPrecio());
    s.setDuracionMin(d.getDuracionMin());
    s = servicios.saveAndFlush(s);
    log.info("Servicio guardado id={}", s.getId());
    return AutoAndesMapper.servicio(s);
  }

  @Transactional
  public void delete(Long id) {
    Servicio s = entidad(id);
    if (detalles.existsByServicioId(id))
      throw new ApiException(409, "Servicio utilizado en una orden. Use PATCH para darlo de baja");
    servicios.delete(s);
    log.info("Servicio eliminado id={}", id);
  }

  @Transactional
  public ServicioResponseDTO cambiarEstado(Long id, boolean activo) {
    Servicio s = entidad(id);
    s.setActivo(activo);
    log.info("Estado servicio id={} activo={}", id, activo);
    return AutoAndesMapper.servicio(s);
  }
}
