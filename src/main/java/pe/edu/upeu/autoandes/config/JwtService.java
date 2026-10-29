package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey clave;
  private final Clock clock;

  public JwtService(@Value("${app.jwt-secret}") String secret, Clock clock) {
    this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.clock = clock;
  }

  public String generarToken(String username, Set<String> permisos) {
    var ahora = clock.instant();
    return Jwts.builder()
        .subject(username)
        .claim("authorities", permisos)
        .issuedAt(Date.from(ahora))
        .expiration(Date.from(ahora.plus(1, ChronoUnit.HOURS)))
        .signWith(clave)
        .compact();
  }

  public String extraerUsername(String token) {
    return Jwts.parser()
        .verifyWith(clave)
        .clock(() -> Date.from(clock.instant()))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }
}
