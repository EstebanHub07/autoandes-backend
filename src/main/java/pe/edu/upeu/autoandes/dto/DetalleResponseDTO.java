package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;

public record DetalleResponseDTO(
    Long id,
    Long servicioId,
    String servicioNombre,
    Integer cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal) {}
