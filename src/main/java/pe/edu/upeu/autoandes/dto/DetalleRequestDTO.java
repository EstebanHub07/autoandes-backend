package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record DetalleRequestDTO(
    @NotNull @Positive Long servicioId, @NotNull @Min(1) @Max(5) Integer cantidad) {}
