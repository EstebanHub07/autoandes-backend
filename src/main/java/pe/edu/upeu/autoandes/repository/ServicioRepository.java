package pe.edu.upeu.autoandes.repository;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import pe.edu.upeu.autoandes.entity.*;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
  boolean existsByCodigoAndIdNot(String codigo, Long id);
}
