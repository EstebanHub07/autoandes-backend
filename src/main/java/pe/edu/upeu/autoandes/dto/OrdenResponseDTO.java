package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponseDTO {
  private Long id;

  private String numero;

  private LocalDateTime fecha;

  private Long vehiculoId;

  private String placa;

  private String clienteNombre;

  private Integer kilometraje;

  private String estado;

  private BigDecimal total;

  private String usuarioRegistro;

  private String motivoAnulacion;

  private List<DetalleResponseDTO> detalles;
}
