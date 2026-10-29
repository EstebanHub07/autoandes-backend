package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public record DetalleRequestDTO(@NotNull @Positive Long servicioId, @NotNull @Min(1) @Max(5) Integer cantidad) {}

