package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record LoginRequestDTO(@NotBlank String username, @NotBlank String password) {}
