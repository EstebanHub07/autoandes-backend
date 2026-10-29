package pe.edu.upeu.autoandes.controller;
import pe.edu.upeu.autoandes.service.service.ClienteService;import pe.edu.upeu.autoandes.dto.*;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/v1/clientes")
public class ClienteController {
private final ClienteService service;public ClienteController(ClienteService s){service=s;}
@GetMapping public List<ClienteResponseDTO> listar(){return service.readAll();}
}
