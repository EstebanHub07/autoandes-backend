package pe.edu.upeu.autoandes.service.service;
import pe.edu.upeu.autoandes.service.generic.CrudService;import pe.edu.upeu.autoandes.dto.*;import java.util.List;
public interface VehiculoService extends CrudService<VehiculoRequestDTO,VehiculoResponseDTO,Long> { List<VehiculoResponseDTO> buscarPorCliente(Long clienteId); }
