package pe.edu.upeu.autoandes.config;
import org.springframework.context.annotation.*;import org.springframework.http.HttpMethod;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.web.SecurityFilterChain;import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.security.core.userdetails.UserDetailsService;import pe.edu.upeu.autoandes.security.*;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
@Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
@Bean SecurityFilterChain filterChain(HttpSecurity http,JwtService jwt,UserDetailsService usuarios,RespuestaErrorSeguridad errores,RestAuthenticationEntryPoint entry,RestAccessDeniedHandler denied)throws Exception{
return http.csrf(c->c.disable()).logout(c->c.disable()).cors(c->{})
.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(a->a.requestMatchers("/api/v1/auth/login","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll()
.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
.requestMatchers("/api/v1/auth/me").authenticated()
.requestMatchers(HttpMethod.GET,"/api/v1/clientes").hasAuthority("VEHICULO_LEER")
.requestMatchers(HttpMethod.GET,"/api/v1/servicios/**").hasAuthority("SERVICIO_LEER")
.requestMatchers("/api/v1/servicios/**").hasAuthority("SERVICIO_ESCRIBIR")
.requestMatchers(HttpMethod.GET,"/api/v1/vehiculos/**").hasAuthority("VEHICULO_LEER")
.requestMatchers("/api/v1/vehiculos/**").hasAuthority("VEHICULO_ESCRIBIR")
.requestMatchers(HttpMethod.POST,"/api/v1/ordenes").hasAuthority("ORDEN_REGISTRAR")
.requestMatchers(HttpMethod.GET,"/api/v1/ordenes/**").hasAuthority("ORDEN_LEER")
.requestMatchers(HttpMethod.PATCH,"/api/v1/ordenes/*/anular").hasAuthority("ORDEN_ANULAR")
.requestMatchers("/api/v1/reportes/**").hasAuthority("REPORTE_VER")
.anyRequest().authenticated())
.exceptionHandling(e->e.authenticationEntryPoint(entry).accessDeniedHandler(denied))
.addFilterBefore(new JwtAuthenticationFilter(jwt,usuarios,errores),UsernamePasswordAuthenticationFilter.class).build();
}
}
