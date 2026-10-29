package pe.edu.upeu.autoandes.service.service;
import pe.edu.upeu.autoandes.dto.*;
public interface AuthService { LoginResponseDTO login(LoginRequestDTO d);UsuarioResponseDTO usuarioActual(String username); }
