package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;

public record ServicioResponseDTO(
    Long id,
    String codigo,
    String nombre,
    BigDecimal precio,
    Integer duracionMin,
    Boolean activo) {}
