package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.EstadoOrden;
import pe.edu.upeu.autoandes.service.service.OrdenService;

@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenController {
  private final OrdenService service;

  public OrdenController(OrdenService s) {
    service = s;
  }

  @PostMapping
  public ResponseEntity<OrdenResponseDTO> registrar(
      @Valid @RequestBody OrdenRequestDTO d, Authentication a) {
    return ResponseEntity.status(201).body(service.registrar(d, a.getName()));
  }

  @GetMapping
  public List<OrdenResponseDTO> buscar(
      @RequestParam(required = false) String placa,
      @RequestParam(required = false) EstadoOrden estado,
      @RequestParam(required = false) LocalDate desde,
      @RequestParam(required = false) LocalDate hasta) {
    return service.buscar(placa, estado, desde, hasta);
  }

  @GetMapping("/{id}")
  public OrdenResponseDTO obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PatchMapping("/{id}/anular")
  public OrdenResponseDTO anular(@PathVariable Long id, @Valid @RequestBody AnularRequestDTO d) {
    return service.anular(id, d);
  }
}
