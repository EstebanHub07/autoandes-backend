package pe.edu.upeu.autoandes.mapper;

import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.*;

public final class AutoAndesMapper {
  private AutoAndesMapper() {}

  public static ClienteResponseDTO cliente(Cliente c) {
    return new ClienteResponseDTO(c.getId(), c.getDocumento(), c.getNombres(), c.getTelefono());
  }

  public static ServicioResponseDTO servicio(Servicio s) {
    return new ServicioResponseDTO(
        s.getId(), s.getCodigo(), s.getNombre(), s.getPrecio(), s.getDuracionMin(), s.getActivo());
  }

  public static VehiculoResponseDTO vehiculo(Vehiculo v) {
    return new VehiculoResponseDTO(
        v.getId(),
        v.getPlaca(),
        v.getMarca(),
        v.getModelo(),
        v.getAnio(),
        v.getCliente().getId(),
        v.getCliente().getNombres());
  }

  public static OrdenResponseDTO orden(OrdenServicio o) {
    return new OrdenResponseDTO(
        o.getId(),
        o.getNumero(),
        o.getFecha(),
        o.getVehiculo().getId(),
        o.getVehiculo().getPlaca(),
        o.getVehiculo().getCliente().getNombres(),
        o.getKilometraje(),
        o.getEstado().name(),
        o.getTotal(),
        o.getUsuarioRegistro(),
        o.getMotivoAnulacion(),
        o.getDetalles().stream()
            .map(
                d ->
                    new DetalleResponseDTO(
                        d.getId(),
                        d.getServicio().getId(),
                        d.getServicio().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
            .toList());
  }
}
