package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record UsuarioResponseDTO(
    String username, String nombreCompleto, Set<String> roles, Set<String> permisos) {}
