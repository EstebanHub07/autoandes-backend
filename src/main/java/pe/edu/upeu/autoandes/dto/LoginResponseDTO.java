package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
  private String token;

  private String tipo;

  private long expiraEnSegundos;

  private String nombreCompleto;

  private Set<String> roles;

  private Set<String> permisos;
}
