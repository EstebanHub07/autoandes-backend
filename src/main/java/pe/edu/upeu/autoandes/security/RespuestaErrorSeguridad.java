package pe.edu.upeu.autoandes.security;
import jakarta.servlet.http.*;import java.io.IOException;import org.springframework.stereotype.Component;import org.springframework.http.HttpStatus;import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;import tools.jackson.databind.ObjectMapper;import java.time.*;
@Component
public class RespuestaErrorSeguridad {
private final ObjectMapper mapper;private final Clock clock;
public RespuestaErrorSeguridad(ObjectMapper mapper,Clock clock){this.mapper=mapper;this.clock=clock;}
public void escribir(HttpServletRequest req,HttpServletResponse res,int status,String mensaje)throws IOException{
res.setStatus(status);res.setContentType("application/json");res.setCharacterEncoding("UTF-8");
res.getWriter().write(mapper.writeValueAsString(new ErrorResponseDTO(LocalDateTime.now(clock),status,HttpStatus.valueOf(status).getReasonPhrase(),mensaje,req.getRequestURI(),null)));
}
}
