package pe.edu.upeu.autoandes.service.impl;
import pe.edu.upeu.autoandes.service.service.AuthService;import pe.edu.upeu.autoandes.repository.UsuarioRepository;import pe.edu.upeu.autoandes.dto.*;import pe.edu.upeu.autoandes.security.PermisosPorRol;import pe.edu.upeu.autoandes.config.JwtService;import pe.edu.upeu.autoandes.exception.ApiException;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.security.crypto.password.PasswordEncoder;import java.util.*;import java.util.stream.Collectors;
@Service @Transactional(readOnly=true)
public class AuthServiceImpl implements AuthService {
private final UsuarioRepository usuarios;private final PasswordEncoder encoder;private final JwtService jwt;private final String dummyHash;
public AuthServiceImpl(UsuarioRepository u,PasswordEncoder e,JwtService j){usuarios=u;encoder=e;jwt=j;dummyHash=e.encode(UUID.randomUUID().toString());}
public LoginResponseDTO login(LoginRequestDTO d){
var usuario=usuarios.findByUsername(d.username().trim());
String hash=usuario.map(u->u.getPassword()).orElse(dummyHash);
boolean coincide=encoder.matches(d.password(),hash==null?dummyHash:hash);
if(!coincide||usuario.isEmpty()||!Boolean.TRUE.equals(usuario.get().getActivo()))throw new ApiException(401,"Usuario o contraseña incorrectos");
var u=usuario.get();var roles=u.getRoles().stream().map(r->r.getNombre()).collect(Collectors.toCollection(TreeSet::new));var permisos=PermisosPorRol.permisos(roles);
return new LoginResponseDTO(jwt.generarToken(u.getUsername(),permisos),"Bearer",3600,u.getNombrecompleto(),roles,permisos);
}
public UsuarioResponseDTO usuarioActual(String username){var u=usuarios.findByUsername(username).orElseThrow(()->new ApiException(401,"Usuario o contraseña incorrectos"));var roles=u.getRoles().stream().map(r->r.getNombre()).collect(Collectors.toCollection(TreeSet::new));return new UsuarioResponseDTO(u.getUsername(),u.getNombrecompleto(),roles,PermisosPorRol.permisos(roles));}
}
