package pe.edu.upeu.autoandes.security;

import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class PermisosPorRol {
  private PermisosPorRol() {}

  private static final Map<String, Set<Permiso>> PERMISOS =
      Map.of(
          "ADMIN", Collections.unmodifiableSet(EnumSet.allOf(Permiso.class)),
          "ASESOR",
              Set.of(
                  Permiso.SERVICIO_LEER,
                  Permiso.VEHICULO_LEER,
                  Permiso.VEHICULO_ESCRIBIR,
                  Permiso.ORDEN_LEER,
                  Permiso.ORDEN_REGISTRAR),
          "JEFE_TALLER",
              Set.of(
                  Permiso.SERVICIO_LEER,
                  Permiso.SERVICIO_ESCRIBIR,
                  Permiso.VEHICULO_LEER,
                  Permiso.ORDEN_LEER,
                  Permiso.ORDEN_ANULAR,
                  Permiso.REPORTE_VER));

  public static Set<String> permisos(Collection<String> roles) {
    Set<String> resultado = new TreeSet<>();
    roles.forEach(
        rol -> PERMISOS.getOrDefault(rol, Set.of()).forEach(p -> resultado.add(p.name())));
    return resultado;
  }

  public static List<GrantedAuthority> authorities(Collection<String> roles) {
    return permisos(roles).stream()
        .map(p -> (GrantedAuthority) new SimpleGrantedAuthority(p))
        .toList();
  }
}
