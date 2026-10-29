package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.upeu.autoandes.security.RespuestaErrorSeguridad;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserDetailsService usuarios;
  private final RespuestaErrorSeguridad errores;

  public JwtAuthenticationFilter(JwtService j, UserDetailsService u, RespuestaErrorSeguridad e) {
    jwt = j;
    usuarios = u;
    errores = e;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest r) {
    String p = r.getServletPath();
    return "OPTIONS".equals(r.getMethod())
        || p.equals("/api/v1/auth/login")
        || p.startsWith("/swagger-ui")
        || p.startsWith("/v3/api-docs");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      try {
        String username = jwt.extraerUsername(header.substring(7).trim());
        if (username == null || username.isBlank()) throw new IllegalArgumentException();
        var user = usuarios.loadUserByUsername(username);
        if (!user.isEnabled()) throw new IllegalArgumentException();
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
      } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
        SecurityContextHolder.clearContext();
        errores.escribir(req, res, 401, "Token inválido, vencido o usuario inactivo");
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
