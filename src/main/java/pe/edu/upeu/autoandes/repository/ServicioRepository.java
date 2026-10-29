package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ServicioRepository extends JpaRepository<Servicio,Long> {
boolean existsByCodigoAndIdNot(String codigo, Long id);
}

