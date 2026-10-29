package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface VehiculoRepository extends JpaRepository<Vehiculo,Long> {
boolean existsByPlacaAndIdNot(String placa,Long id);
List<Vehiculo> findByClienteId(Long clienteId);
@Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
@Query("select v from Vehiculo v where v.id=:id") Optional<Vehiculo> bloquear(@Param("id") Long id);
}

