package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 7)
  private String placa;

  @Column(nullable = false, length = 40)
  private String marca;

  @Column(nullable = false, length = 40)
  private String modelo;

  @Column(nullable = false)
  private Integer anio;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getPlaca() {
    return placa;
  }

  public void setPlaca(String value) {
    this.placa = value;
  }

  public String getMarca() {
    return marca;
  }

  public void setMarca(String value) {
    this.marca = value;
  }

  public String getModelo() {
    return modelo;
  }

  public void setModelo(String value) {
    this.modelo = value;
  }

  public Integer getAnio() {
    return anio;
  }

  public void setAnio(Integer value) {
    this.anio = value;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente value) {
    this.cliente = value;
  }
}
