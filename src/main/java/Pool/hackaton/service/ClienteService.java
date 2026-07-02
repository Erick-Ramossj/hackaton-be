package Pool.hackaton.service;

import Pool.hackaton.entity.Cliente;
import Pool.hackaton.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * SERVICIO: Cliente
 * ============================================================
 * Lógica de negocio para clientes.
 *
 * ¿CÓMO AGREGAR UNA NUEVA VALIDACIÓN?
 * -------------------------------------------------------------
 * Ejemplo: validar que el email no esté duplicado
 * 1. En ClienteRepository agregar:
 *       boolean existsByEmail(String email);
 * 2. Aquí en guardar():
 *       if (clienteRepository.existsByEmail(cliente.getEmail())) {
 *           throw new RuntimeException("Email ya registrado");
 *       }
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Solo clientes activos (estado = true)
    public List<Cliente> listar() {
        return clienteRepository.findByEstadoTrue();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        // Valida DNI duplicado tanto al crear como al editar,
        // excluyendo al propio cliente en caso de actualización
        Integer idActual = cliente.getIdCliente() == null ? -1 : cliente.getIdCliente();
        if (clienteRepository.existsByDniAndIdClienteNot(cliente.getDni(), idActual)) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }
        return clienteRepository.save(cliente);
    }

    // Baja lógica
    public boolean eliminar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(false);
            clienteRepository.save(c);
            return true;
        }
        return false;
    }
}
