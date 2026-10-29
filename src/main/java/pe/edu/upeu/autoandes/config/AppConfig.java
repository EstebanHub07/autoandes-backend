package pe.edu.upeu.autoandes.config;

import java.time.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {
  @Bean
  public Clock clock(@Value("${app.zone:America/Lima}") String zona) {
    return Clock.system(ZoneId.of(zona));
  }
}
