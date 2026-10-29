package pe.edu.upeu.autoandes.repository;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.autoandes.entity.*;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
  boolean existsByPlacaAndIdNot(String placa, Long id);

  List<Vehiculo> findByClienteId(Long clienteId);

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select v from Vehiculo v where v.id=:id")
  Optional<Vehiculo> bloquear(@Param("id") Long id);
}
