package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_orden")
public class DetalleOrden {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "orden_id")
  private OrdenServicio orden;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "servicio_id")
  private Servicio servicio;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal precioUnitario;

  @Column(nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal subtotal;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public OrdenServicio getOrden() {
    return orden;
  }

  public void setOrden(OrdenServicio value) {
    this.orden = value;
  }

  public Servicio getServicio() {
    return servicio;
  }

  public void setServicio(Servicio value) {
    this.servicio = value;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer value) {
    this.cantidad = value;
  }

  public java.math.BigDecimal getPrecioUnitario() {
    return precioUnitario;
  }

  public void setPrecioUnitario(java.math.BigDecimal value) {
    this.precioUnitario = value;
  }

  public java.math.BigDecimal getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(java.math.BigDecimal value) {
    this.subtotal = value;
  }
}
