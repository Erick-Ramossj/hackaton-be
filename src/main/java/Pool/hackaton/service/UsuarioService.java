package Pool.hackaton.service;

import Pool.hackaton.entity.Usuario;
import Pool.hackaton.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * SERVICIO: Usuario
 * ============================================================
 * Contiene toda la lógica de negocio para usuarios.
 *
 * FLUJO DE UNA PETICIÓN:
 *   Frontend → Controller → Service → Repository → BD
 *
 * @RequiredArgsConstructor (Lombok): genera el constructor con
 * todos los campos "final", que es como Spring inyecta dependencias.
 * Es equivalente a poner @Autowired pero más limpio.
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * LOGIN SIMPLE
     * Retorna Optional: si encuentra el usuario → Optional.of(usuario)
     * Si no lo encuentra → Optional.empty()
     * El controller decide qué respuesta HTTP enviar según el resultado.
     */
    public Optional<Usuario> login(String usuario, String password) {
        return usuarioRepository.findByUsuarioAndPassword(usuario, password);
    }

    // Retorna todos los usuarios (activos e inactivos)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Sirve tanto para crear (id = null) como para actualizar (id presente)
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * BAJA LÓGICA
     * No borra el registro de la BD, solo cambia estado a false.
     * Esto preserva la integridad referencial (si el usuario tiene
     * ventas asociadas, no se puede borrar físicamente).
     */
    public boolean eliminar(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setEstado(false);
            usuarioRepository.save(u);
            return true;
        }
        return false; // No existe
    }
}
