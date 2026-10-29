package pe.edu.upeu.autoandes.security;
import jakarta.servlet.http.*;import java.io.IOException;import org.springframework.stereotype.Component;import org.springframework.security.web.access.AccessDeniedHandler;import org.springframework.security.access.AccessDeniedException;
@Component public class RestAccessDeniedHandler implements AccessDeniedHandler {
private final RespuestaErrorSeguridad respuesta;
public RestAccessDeniedHandler(RespuestaErrorSeguridad r){respuesta=r;}
public void handle(HttpServletRequest req,HttpServletResponse res,AccessDeniedException e)throws IOException{respuesta.escribir(req,res,403,"No tiene permiso para esta operación");}
}
