package pe.edu.upeu.autoandes.service.service;
import pe.edu.upeu.autoandes.service.generic.CrudService;import pe.edu.upeu.autoandes.dto.*;
public interface ServicioService extends CrudService<ServicioRequestDTO,ServicioResponseDTO,Long> { ServicioResponseDTO cambiarEstado(Long id,boolean activo); }
