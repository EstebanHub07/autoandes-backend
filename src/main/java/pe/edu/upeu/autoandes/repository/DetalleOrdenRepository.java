package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden,Long> {
boolean existsByServicioId(Long servicioId);
}

