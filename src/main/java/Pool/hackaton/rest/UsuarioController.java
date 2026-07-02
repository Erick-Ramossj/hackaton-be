package Pool.hackaton.rest;

import Pool.hackaton.dto.UsuarioLoginResponseDTO;
import Pool.hackaton.model.Usuario;
import Pool.hackaton.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CONTROLLER: Usuario
 * Base URL: /api/usuarios
 * ============================================================
 * RESUMEN DE ENDPOINTS:
 *   GET    /api/usuarios          â†’ listar todos
 *   GET    /api/usuarios/{id}     â†’ buscar por ID
 *   POST   /api/usuarios          â†’ crear
 *   PUT    /api/usuarios/{id}     â†’ actualizar
 *   DELETE /api/usuarios/{id}     â†’ baja lÃ³gica
 *   POST   /api/usuarios/login    â†’ autenticaciÃ³n
 * ============================================================
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioService.buscarPorId(id).map(existente -> {
            usuario.setIdUsuario(id);
            return ResponseEntity.ok(usuarioService.guardar(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = usuarioService.eliminar(id);
        return ok ? ResponseEntity.ok("Usuario desactivado")
                  : ResponseEntity.notFound().build();
    }

    // PUT /api/usuarios/{id}/restaurar → restauración lógica
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<String> restaurar(@PathVariable Integer id) {
        boolean ok = usuarioService.restaurar(id);
        return ok ? ResponseEntity.ok("Usuario restaurado")
                  : ResponseEntity.notFound().build();
    }

    /**
     * POST /api/usuarios/login
     * Recibe: { "usuario": "admin", "password": "12345" }
     * Retorna: el objeto Usuario si las credenciales son correctas (200)
     *          o "Credenciales incorrectas" si no coinciden (401)
     *
     * Map<String, String> permite recibir un JSON simple sin crear un DTO
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario  = credenciales.get("usuario");
        String password = credenciales.get("password");

        return usuarioService.login(usuario, password)
                .map(u -> ResponseEntity.ok((Object) UsuarioLoginResponseDTO.from(u)))
                .orElse(ResponseEntity.status(401).body("Credenciales incorrectas"));
    }
}


