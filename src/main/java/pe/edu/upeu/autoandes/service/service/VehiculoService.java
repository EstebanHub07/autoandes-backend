package pe.edu.upeu.autoandes.service.service;

import java.util.List;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.generic.CrudService;

public interface VehiculoService
    extends CrudService<VehiculoRequestDTO, VehiculoResponseDTO, Long> {
  List<VehiculoResponseDTO> buscarPorCliente(Long clienteId);
}
