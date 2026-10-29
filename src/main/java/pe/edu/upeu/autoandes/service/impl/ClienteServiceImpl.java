package pe.edu.upeu.autoandes.service.impl;
import pe.edu.upeu.autoandes.service.service.ClienteService;import pe.edu.upeu.autoandes.repository.ClienteRepository;import pe.edu.upeu.autoandes.dto.ClienteResponseDTO;import pe.edu.upeu.autoandes.mapper.AutoAndesMapper;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.List;
@Service @Transactional(readOnly=true) public class ClienteServiceImpl implements ClienteService {
private final ClienteRepository clientes;
public ClienteServiceImpl(ClienteRepository c){clientes=c;}
public List<ClienteResponseDTO> readAll(){return clientes.findAll().stream().map(AutoAndesMapper::cliente).toList();}
}
