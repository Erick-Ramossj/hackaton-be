package Pool.hackaton.service;

import Pool.hackaton.model.Cliente;
import Pool.hackaton.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() {
        return clienteRepository.findByEstadoTrue();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        LocalDateTime ahora = LocalDateTime.now();
        Integer idActual = cliente.getIdCliente() == null ? -1 : cliente.getIdCliente();

        // Validar DNI duplicado tanto al crear como al editar
        if (clienteRepository.existsByDniAndIdClienteNot(cliente.getDni(), idActual)) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }

        if (cliente.getIdCliente() == null) {
            // CREAR
            cliente.setCreatedAt(ahora);
            cliente.setUpdatedAt(null);
            cliente.setDeletedAt(null);
            cliente.setRestoredAt(null);
        } else {
            // ACTUALIZAR: conservar created_at original
            clienteRepository.findById(cliente.getIdCliente()).ifPresent(
                existente -> cliente.setCreatedAt(existente.getCreatedAt())
            );
            cliente.setUpdatedAt(ahora);
        }
        return clienteRepository.save(cliente);
    }

    // Baja lógica
    public boolean eliminar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(false);
            c.setDeletedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            clienteRepository.save(c);
            return true;
        }
        return false;
    }

    // Restauración lógica
    public boolean restaurar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(true);
            c.setRestoredAt(LocalDateTime.now());
            c.setDeletedAt(null);
            c.setUpdatedAt(LocalDateTime.now());
            clienteRepository.save(c);
            return true;
        }
        return false;
    }
}
