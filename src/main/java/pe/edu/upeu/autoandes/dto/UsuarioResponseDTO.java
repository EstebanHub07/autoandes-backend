package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public record UsuarioResponseDTO(String username, String nombreCompleto, Set<String> roles, Set<String> permisos) {}

