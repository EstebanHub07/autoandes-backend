package pe.edu.upeu.autoandes.controller;
import pe.edu.upeu.autoandes.service.service.VehiculoService;import pe.edu.upeu.autoandes.dto.*;import org.springframework.web.bind.annotation.*;import org.springframework.http.*;import jakarta.validation.Valid;import java.util.List;
@RestController @RequestMapping("/api/v1/vehiculos")
public class VehiculoController {
private final VehiculoService service;public VehiculoController(VehiculoService s){service=s;}
@GetMapping public List<VehiculoResponseDTO> listar(@RequestParam(required=false) Long clienteId){return service.buscarPorCliente(clienteId);}
@GetMapping("/{id}") public VehiculoResponseDTO obtener(@PathVariable Long id){return service.read(id);}
@PostMapping public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoRequestDTO d){return ResponseEntity.status(201).body(service.create(d));}
@PutMapping("/{id}") public VehiculoResponseDTO editar(@PathVariable Long id,@Valid @RequestBody VehiculoRequestDTO d){return service.update(id,d);}
@DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}

}
