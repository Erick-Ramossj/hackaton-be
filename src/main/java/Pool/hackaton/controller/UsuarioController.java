package Pool.hackaton.controller;

import Pool.hackaton.dto.UsuarioLoginResponseDTO;
import Pool.hackaton.entity.Usuario;
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
 *   GET    /api/usuarios          → listar todos
 *   GET    /api/usuarios/{id}     → buscar por ID
 *   POST   /api/usuarios          → crear
 *   PUT    /api/usuarios/{id}     → actualizar
 *   DELETE /api/usuarios/{id}     → baja lógica
 *   POST   /api/usuarios/login    → autenticación
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
