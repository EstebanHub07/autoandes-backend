package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*;
@Entity @Table(name="servicio")
public class Servicio {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@Column(nullable=false,unique=true,length=7) private String codigo;
@Column(nullable=false,length=80) private String nombre;
@Column(nullable=false,precision=10,scale=2) private java.math.BigDecimal precio;
@Column(name="duracion_min",nullable=false) private Integer duracionMin;
@Column(nullable=false) @Convert(converter=org.hibernate.type.NumericBooleanConverter.class) private Boolean activo=true;

public Long getId(){return id;}
public void setId(Long value){this.id=value;}

public String getCodigo(){return codigo;}
public void setCodigo(String value){this.codigo=value;}

public String getNombre(){return nombre;}
public void setNombre(String value){this.nombre=value;}

public java.math.BigDecimal getPrecio(){return precio;}
public void setPrecio(java.math.BigDecimal value){this.precio=value;}

public Integer getDuracionMin(){return duracionMin;}
public void setDuracionMin(Integer value){this.duracionMin=value;}

public Boolean getActivo(){return activo;}
public void setActivo(Boolean value){this.activo=value;}
}

