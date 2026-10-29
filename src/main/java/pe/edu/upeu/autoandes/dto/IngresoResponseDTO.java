package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;

public record IngresoResponseDTO(
    String codigo, String servicio, Long cantidadTotal, BigDecimal importeTotal) {}
