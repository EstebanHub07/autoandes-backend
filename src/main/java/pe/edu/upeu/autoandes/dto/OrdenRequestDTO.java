package pe.edu.upeu.autoandes.dto;

import jakarta.validation.Valid;
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
public class OrdenRequestDTO {
  @NotNull @Positive private Long vehiculoId;

  @NotNull
  @Min(0)
  @Max(9999999)
  private Integer kilometraje;

  @NotNull
  @Size(min = 1, max = 10)
  private List<@NotNull @Valid DetalleRequestDTO> detalles;
}
