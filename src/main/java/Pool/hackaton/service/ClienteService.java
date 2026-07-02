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
        // Al CREAR (id es null), verificar que el DNI no esté duplicado
        // Al ACTUALIZAR (id presente), se omite esta validación
        if (cliente.getIdCliente() == null && clienteRepository.existsByDni(cliente.getDni())) {
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
