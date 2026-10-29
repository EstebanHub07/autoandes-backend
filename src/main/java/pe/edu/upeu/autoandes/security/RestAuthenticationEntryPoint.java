package pe.edu.upeu.autoandes.security;
import jakarta.servlet.http.*;import java.io.IOException;import org.springframework.stereotype.Component;import org.springframework.security.web.AuthenticationEntryPoint;import org.springframework.security.core.AuthenticationException;
@Component public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
private final RespuestaErrorSeguridad respuesta;
public RestAuthenticationEntryPoint(RespuestaErrorSeguridad r){respuesta=r;}
public void commence(HttpServletRequest req,HttpServletResponse res,AuthenticationException e)throws IOException{
res.setHeader("WWW-Authenticate","Bearer");respuesta.escribir(req,res,401,"Debe iniciar sesión con un token válido");}
}
