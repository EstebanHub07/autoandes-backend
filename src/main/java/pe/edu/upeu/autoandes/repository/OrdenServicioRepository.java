package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio,Long> {
boolean existsByVehiculoId(Long vehiculoId);
Optional<OrdenServicio> findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(Long vehiculoId,EstadoOrden estado);
@Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
@Query("select o from OrdenServicio o where o.id=:id") Optional<OrdenServicio> bloquear(@Param("id") Long id);
@Query(value="select orden_numero_seq.nextval from dual",nativeQuery=true) Long siguienteNumero();
@Query("""
select o from OrdenServicio o join fetch o.vehiculo v join fetch v.cliente
where (:placa is null or upper(v.placa) like concat('%',concat(upper(:placa),'%')))
and (:estado is null or o.estado=:estado)
and (:desde is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta)
order by o.fecha desc, o.id desc
""")
List<OrdenServicio> buscar(@Param("placa") String placa,@Param("estado") EstadoOrden estado,@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta);
@Query("""
select new pe.edu.upeu.autoandes.dto.IngresoResponseDTO(s.codigo,s.nombre,sum(d.cantidad),sum(d.subtotal))
from DetalleOrden d join d.servicio s join d.orden o
where o.estado=pe.edu.upeu.autoandes.entity.EstadoOrden.REGISTRADA
and (:desde is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta)
group by s.codigo,s.nombre order by sum(d.subtotal) desc
""")
List<pe.edu.upeu.autoandes.dto.IngresoResponseDTO> ingresos(@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta);
}

