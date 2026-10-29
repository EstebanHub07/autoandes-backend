package pe.edu.upeu.autoandes.service.service;

import java.time.LocalDate;
import java.util.List;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.EstadoOrden;

public interface OrdenService {
  OrdenResponseDTO registrar(OrdenRequestDTO d, String username);

  OrdenResponseDTO anular(Long id, AnularRequestDTO d);

  OrdenResponseDTO obtener(Long id);

  List<OrdenResponseDTO> buscar(String placa, EstadoOrden estado, LocalDate desde, LocalDate hasta);

  List<IngresoResponseDTO> ingresos(LocalDate desde, LocalDate hasta);
}
