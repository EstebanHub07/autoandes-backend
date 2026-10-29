package pe.edu.upeu.autoandes.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
  private final ClienteService service;

  public ClienteController(ClienteService s) {
    service = s;
  }

  @GetMapping
  public List<ClienteResponseDTO> listar() {
    return service.readAll();
  }
}
