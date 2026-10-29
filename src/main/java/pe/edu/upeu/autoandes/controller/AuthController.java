package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService s) {
    service = s;
  }

  @PostMapping("/login")
  @io.swagger.v3.oas.annotations.security.SecurityRequirements
  public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO d) {
    return service.login(d);
  }

  @GetMapping("/me")
  public UsuarioResponseDTO me(Authentication a) {
    return service.usuarioActual(a.getName());
  }
}
