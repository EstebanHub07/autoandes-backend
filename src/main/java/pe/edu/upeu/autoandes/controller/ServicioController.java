package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.service.ServicioService;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {
  private final ServicioService service;

  public ServicioController(ServicioService s) {
    service = s;
  }

  @GetMapping
  public List<ServicioResponseDTO> listar() {
    return service.readAll();
  }

  @GetMapping("/{id}")
  public ServicioResponseDTO obtener(@PathVariable Long id) {
    return service.read(id);
  }

  @PostMapping
  public ResponseEntity<ServicioResponseDTO> crear(@Valid @RequestBody ServicioRequestDTO d) {
    return ResponseEntity.status(201).body(service.create(d));
  }

  @PutMapping("/{id}")
  public ServicioResponseDTO editar(
      @PathVariable Long id, @Valid @RequestBody ServicioRequestDTO d) {
    return service.update(id, d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/estado")
  public ServicioResponseDTO estado(@PathVariable Long id, @Valid @RequestBody EstadoRequestDTO d) {
    return service.cambiarEstado(id, d.getActivo());
  }
}
