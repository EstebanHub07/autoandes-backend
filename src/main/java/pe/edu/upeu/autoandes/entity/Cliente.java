package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 11)
  private String documento;

  @Column(nullable = false, length = 120)
  private String nombres;

  @Column(length = 15)
  private String telefono;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String value) {
    this.documento = value;
  }

  public String getNombres() {
    return nombres;
  }

  public void setNombres(String value) {
    this.nombres = value;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String value) {
    this.telefono = value;
  }
}
