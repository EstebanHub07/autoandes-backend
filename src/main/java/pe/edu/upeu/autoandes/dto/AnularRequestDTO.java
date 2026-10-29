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
public class AnularRequestDTO {
  @NotBlank
  @Size(min = 10, max = 200)
  private String motivo;
}
