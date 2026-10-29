package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRequestDTO {
  @NotNull @Positive private Long servicioId;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer cantidad;
}
