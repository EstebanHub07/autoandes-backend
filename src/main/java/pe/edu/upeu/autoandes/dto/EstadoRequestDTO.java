package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record EstadoRequestDTO(@NotNull Boolean activo) {}
