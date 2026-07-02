package Pool.hackaton.controller;

import Pool.hackaton.entity.Cliente;
import Pool.hackaton.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Cliente
 * Base URL: /api/clientes
 * ============================================================
 * RESUMEN DE ENDPOINTS:
 *   GET    /api/clientes          → listar activos
 *   GET    /api/clientes/{id}     → buscar por ID
 *   POST   /api/clientes          → crear
 *   PUT    /api/clientes/{id}     → actualizar
 *   DELETE /api/clientes/{id}     → baja lógica
 * ============================================================
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ResponseEntity<?> con "?" permite retornar tanto un Cliente (200)
    // como un String de error (400) según lo que pase en el service
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.ok(clienteService.guardar(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Ej: DNI duplicado
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        return clienteService.buscarPorId(id).map(existente -> {
            cliente.setIdCliente(id);
            return ResponseEntity.ok(clienteService.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = clienteService.eliminar(id);
        return ok ? ResponseEntity.ok("Cliente desactivado")
                  : ResponseEntity.notFound().build();
    }
}
