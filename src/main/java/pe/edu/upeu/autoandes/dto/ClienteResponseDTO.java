package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public record ClienteResponseDTO(Long id, String documento, String nombres, String telefono) {}
