package pe.edu.upeu.autoandes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;

@Component
public class SeedPasswords implements CommandLineRunner {
  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final String password;

  public SeedPasswords(
      UsuarioRepository u, PasswordEncoder e, @Value("${app.seed-password}") String p) {
    usuarios = u;
    encoder = e;
    password = p;
  }

  @Override
  @Transactional
  public void run(String... args) {
    usuarios.findAll().stream()
        .filter(u -> u.getPassword() == null || u.getPassword().isBlank())
        .forEach(u -> u.setPassword(encoder.encode(password)));
  }
}
