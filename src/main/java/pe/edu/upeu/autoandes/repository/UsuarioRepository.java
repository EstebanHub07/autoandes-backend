package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
@EntityGraph(attributePaths="roles") Optional<Usuario> findByUsername(String username);
}

