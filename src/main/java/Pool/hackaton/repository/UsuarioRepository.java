package Pool.hackaton.repository;

import Pool.hackaton.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORIO: Usuario
 * ============================================================
 * Optional<T> significa que puede o no encontrar el registro.
 * Se usa para evitar NullPointerException.
 * ============================================================
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Busca por nombre de usuario (para verificar si ya existe)
    Optional<Usuario> findByUsuario(String usuario);

    // Login simple: busca por usuario + password
    // En producción se usaría BCrypt, aquí es texto plano por simplicidad
    Optional<Usuario> findByUsuarioAndPassword(String usuario, String password);
}
