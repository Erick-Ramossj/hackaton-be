package Pool.hackaton.service;

import Pool.hackaton.model.Usuario;
import Pool.hackaton.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> login(String usuario, String password) {
        return usuarioRepository.findByUsuarioAndPassword(usuario, password);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getIdUsuario() == null) {
            // CREAR
            usuario.setCreatedAt(ahora);
            usuario.setUpdatedAt(null);
            usuario.setDeletedAt(null);
            usuario.setRestoredAt(null);
        } else {
            // ACTUALIZAR: conservar created_at original
            usuarioRepository.findById(usuario.getIdUsuario()).ifPresent(
                existente -> usuario.setCreatedAt(existente.getCreatedAt())
            );
            usuario.setUpdatedAt(ahora);
        }
        return usuarioRepository.save(usuario);
    }

    // Baja lógica
    public boolean eliminar(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setEstado(false);
            u.setDeletedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }

    // Restauración lógica
    public boolean restaurar(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setEstado(true);
            u.setRestoredAt(LocalDateTime.now());
            u.setDeletedAt(null);
            u.setUpdatedAt(LocalDateTime.now());
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }
}
