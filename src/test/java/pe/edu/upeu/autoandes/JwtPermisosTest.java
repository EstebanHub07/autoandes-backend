package pe.edu.upeu.autoandes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.autoandes.config.JwtService;
import pe.edu.upeu.autoandes.security.PermisosPorRol;

class JwtPermisosTest {
  @Test
  void variosRolesRecibenLaUnion() {
    var permisos = PermisosPorRol.permisos(List.of("ASESOR", "JEFE_TALLER"));
    assertEquals(8, permisos.size());
    assertTrue(PermisosPorRol.permisos(List.of("DESCONOCIDO")).isEmpty());
  }

  @Test
  void tokenExpiraYFirmaAlteradaSeRechaza() {
    String key =
        Base64.getEncoder()
            .encodeToString("12345678901234567890123456789012345678901234567890".getBytes());
    Clock ahora = Clock.fixed(Instant.parse("2026-10-29T15:00:00Z"), ZoneId.of("America/Lima"));
    var jwt = new JwtService(key, ahora);
    String token = jwt.generarToken("asesor", Set.of("ORDEN_LEER"));
    assertEquals("asesor", jwt.extraerUsername(token));
    assertThrows(
        io.jsonwebtoken.ExpiredJwtException.class,
        () ->
            new JwtService(key, Clock.offset(ahora, Duration.ofSeconds(3601)))
                .extraerUsername(token));
    String otra =
        Base64.getEncoder()
            .encodeToString("09876543210987654321098765432109876543210987654321".getBytes());
    assertThrows(
        io.jsonwebtoken.JwtException.class,
        () -> new JwtService(otra, ahora).extraerUsername(token));
  }
}
