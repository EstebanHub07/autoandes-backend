package pe.edu.upeu.autoandes.service.impl;
import pe.edu.upeu.autoandes.service.service.VehiculoService;import pe.edu.upeu.autoandes.dto.*;import pe.edu.upeu.autoandes.entity.*;import pe.edu.upeu.autoandes.repository.*;import pe.edu.upeu.autoandes.mapper.AutoAndesMapper;import pe.edu.upeu.autoandes.exception.ApiException;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.List;
@Service @Transactional(readOnly=true)
public class VehiculoServiceImpl implements VehiculoService {
private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(VehiculoServiceImpl.class);
private final ClienteRepository clientes;private final VehiculoRepository vehiculos;private final OrdenServicioRepository ordenes;
public VehiculoServiceImpl(ClienteRepository c,VehiculoRepository v,OrdenServicioRepository o){clientes=c;vehiculos=v;ordenes=o;}
public List<VehiculoResponseDTO> readAll(){return buscarPorCliente(null);}
public List<VehiculoResponseDTO> buscarPorCliente(Long clienteId){return (clienteId==null?vehiculos.findAll():vehiculos.findByClienteId(clienteId)).stream().map(AutoAndesMapper::vehiculo).toList();}
private Vehiculo entidad(Long id){return vehiculos.findById(id).orElseThrow(()->ApiException.noExiste("Vehículo"));}
public VehiculoResponseDTO read(Long id){return AutoAndesMapper.vehiculo(entidad(id));}
@Transactional public VehiculoResponseDTO create(VehiculoRequestDTO d){return guardar(null,d);}
@Transactional public VehiculoResponseDTO update(Long id,VehiculoRequestDTO d){return guardar(id,d);}
private VehiculoResponseDTO guardar(Long id,VehiculoRequestDTO d){
Vehiculo v=id==null?new Vehiculo():entidad(id);
if(vehiculos.existsByPlacaAndIdNot(d.placa(),id==null?-1L:id))throw new ApiException(409,"La placa ya existe");
v.setCliente(clientes.findById(d.clienteId()).orElseThrow(()->ApiException.noExiste("Cliente")));v.setPlaca(d.placa());v.setMarca(d.marca());v.setModelo(d.modelo());v.setAnio(d.anio());
v=vehiculos.saveAndFlush(v);log.info("Vehículo guardado id={}",v.getId());return AutoAndesMapper.vehiculo(v);}
@Transactional public void delete(Long id){Vehiculo v=entidad(id);if(ordenes.existsByVehiculoId(id))throw new ApiException(409,"No se puede eliminar un vehículo con órdenes");vehiculos.delete(v);log.info("Vehículo eliminado id={}",id);}
}
