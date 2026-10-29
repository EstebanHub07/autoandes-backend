package pe.edu.upeu.autoandes.config;
import org.springframework.context.annotation.*;import org.springframework.beans.factory.annotation.Value;import java.time.*;
@Configuration public class AppConfig {
@Bean public Clock clock(@Value("${app.zone:America/Lima}") String zona){return Clock.system(ZoneId.of(zona));}
}
