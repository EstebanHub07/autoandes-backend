package pe.edu.upeu.autoandes.security;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.*;
@Service
public class UsuarioDetailsService implements UserDetailsService {
private final UsuarioRepository repository;
public UsuarioDetailsService(UsuarioRepository r){repository=r;}
@Override @Transactional(readOnly=true)
public UserDetails loadUserByUsername(String username){
var u=repository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Credenciales inválidas"));
return User.withUsername(u.getUsername()).password(u.getPassword()==null?"":u.getPassword())
.disabled(!Boolean.TRUE.equals(u.getActivo())).authorities(PermisosPorRol.authorities(u.getRoles().stream().map(r->r.getNombre()).toList())).build();
}
}
