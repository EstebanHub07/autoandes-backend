package pe.edu.upeu.autoandes.exception;

public class ApiException extends RuntimeException {
  public final int status;

  public ApiException(int status, String mensaje) {
    super(mensaje);
    this.status = status;
  }

  public static ApiException noExiste(String recurso) {
    return new ApiException(404, recurso + " no encontrado");
  }
}
