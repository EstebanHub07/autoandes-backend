package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record LoginResponseDTO(
    String token,
    String tipo,
    long expiraEnSegundos,
    String nombreCompleto,
    Set<String> roles,
    Set<String> permisos) {}
