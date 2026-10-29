package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*;
@Entity @Table(name="usuarios")
public class Usuario {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@Column(nullable=false,unique=true,length=30) private String username;
@Column(length=100) private String password;
@Column(nullable=false,length=120) private String nombrecompleto;
@Column(nullable=false) @Convert(converter=org.hibernate.type.NumericBooleanConverter.class) private Boolean activo;
@ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="usuario_roles",joinColumns=@JoinColumn(name="usuario_id"),inverseJoinColumns=@JoinColumn(name="rol_id")) private java.util.Set<Rol> roles=new java.util.HashSet<>();

public Long getId(){return id;}
public void setId(Long value){this.id=value;}

public String getUsername(){return username;}
public void setUsername(String value){this.username=value;}

public String getPassword(){return password;}
public void setPassword(String value){this.password=value;}

public String getNombrecompleto(){return nombrecompleto;}
public void setNombrecompleto(String value){this.nombrecompleto=value;}

public Boolean getActivo(){return activo;}
public void setActivo(Boolean value){this.activo=value;}

public java.util.Set<Rol> getRoles(){return roles;}
public void setRoles(java.util.Set<Rol> value){this.roles=value;}
}

