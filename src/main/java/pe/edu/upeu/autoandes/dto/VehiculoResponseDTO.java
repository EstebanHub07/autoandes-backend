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
public class VehiculoResponseDTO {
  private Long id;

  private String placa;

  private String marca;

  private String modelo;

  private Integer anio;

  private Long clienteId;

  private String clienteNombre;
}
