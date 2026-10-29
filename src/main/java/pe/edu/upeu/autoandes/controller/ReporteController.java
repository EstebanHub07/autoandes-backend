package pe.edu.upeu.autoandes.controller;
import pe.edu.upeu.autoandes.service.service.OrdenService;import pe.edu.upeu.autoandes.dto.*;import org.springframework.web.bind.annotation.*;import java.time.LocalDate;import java.util.List;
@RestController @RequestMapping("/api/v1/reportes")
public class ReporteController {
private final OrdenService service;public ReporteController(OrdenService s){service=s;}
@GetMapping("/ingresos-por-servicio") public List<IngresoResponseDTO> ingresos(@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta){return service.ingresos(desde,hasta);}
}
