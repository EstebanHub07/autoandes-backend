package pe.edu.upeu.autoandes.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ResponseEntity<ErrorResponseDTO> error(
      int status, String msg, HttpServletRequest r, Map<String, String> campos) {
    return ResponseEntity.status(status)
        .body(
            new ErrorResponseDTO(
                LocalDateTime.now(java.time.ZoneId.of("America/Lima")),
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                msg,
                r.getRequestURI(),
                campos));
  }

  @ExceptionHandler(ApiException.class)
  ResponseEntity<ErrorResponseDTO> negocio(ApiException e, HttpServletRequest r) {
    log.warn("Rechazo {}: {}", r.getRequestURI(), e.getMessage());
    return error(e.status, e.getMessage(), r, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponseDTO> validacion(
      MethodArgumentNotValidException e, HttpServletRequest r) {
    Map<String, String> campos = new LinkedHashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(x -> campos.put(x.getField(), x.getDefaultMessage()));
    return error(400, "Datos inválidos", r, campos);
  }

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    HttpMessageNotReadableException.class
  })
  ResponseEntity<ErrorResponseDTO> formato(Exception e, HttpServletRequest r) {
    return error(400, "Formato de solicitud inválido", r, null);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ErrorResponseDTO> integridad(Exception e, HttpServletRequest r) {
    log.warn("Conflicto de integridad {}", r.getRequestURI());
    return error(409, "Registro duplicado o relacionado con otros registros", r, null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ErrorResponseDTO> denegado(Exception e, HttpServletRequest r) {
    return error(403, "No tiene permiso para esta operación", r, null);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponseDTO> inesperado(Exception e, HttpServletRequest r) {
    log.error("Error inesperado", e);
    return error(500, "Error interno del servidor", r, null);
  }
}
