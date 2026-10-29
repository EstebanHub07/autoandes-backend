package pe.edu.upeu.autoandes.repository;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import pe.edu.upeu.autoandes.entity.*;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
  boolean existsByServicioId(Long servicioId);
}
