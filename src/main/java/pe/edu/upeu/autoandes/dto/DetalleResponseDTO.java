package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleResponseDTO {
  private Long id;

  private Long servicioId;

  private String servicioNombre;

  private Integer cantidad;

  private BigDecimal precioUnitario;

  private BigDecimal subtotal;
}
