package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orden_servicio")
public class OrdenServicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 9)
  private String numero;

  @Column(nullable = false)
  private java.time.LocalDateTime fecha;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "vehiculo_id")
  private Vehiculo vehiculo;

  @Column(nullable = false)
  private Integer kilometraje;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 12)
  private EstadoOrden estado;

  @Column(nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal total;

  @Column(name = "usuario_registro", nullable = false, length = 30)
  private String usuarioRegistro;

  @Column(name = "motivo_anulacion", length = 200)
  private String motivoAnulacion;

  @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
  private java.util.List<DetalleOrden> detalles = new java.util.ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String value) {
    this.numero = value;
  }

  public java.time.LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(java.time.LocalDateTime value) {
    this.fecha = value;
  }

  public Vehiculo getVehiculo() {
    return vehiculo;
  }

  public void setVehiculo(Vehiculo value) {
    this.vehiculo = value;
  }

  public Integer getKilometraje() {
    return kilometraje;
  }

  public void setKilometraje(Integer value) {
    this.kilometraje = value;
  }

  public EstadoOrden getEstado() {
    return estado;
  }

  public void setEstado(EstadoOrden value) {
    this.estado = value;
  }

  public java.math.BigDecimal getTotal() {
    return total;
  }

  public void setTotal(java.math.BigDecimal value) {
    this.total = value;
  }

  public String getUsuarioRegistro() {
    return usuarioRegistro;
  }

  public void setUsuarioRegistro(String value) {
    this.usuarioRegistro = value;
  }

  public String getMotivoAnulacion() {
    return motivoAnulacion;
  }

  public void setMotivoAnulacion(String value) {
    this.motivoAnulacion = value;
  }

  public java.util.List<DetalleOrden> getDetalles() {
    return detalles;
  }

  public void setDetalles(java.util.List<DetalleOrden> value) {
    this.detalles = value;
  }
}
