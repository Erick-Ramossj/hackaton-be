package Pool.hackaton.controller;

import Pool.hackaton.dto.VentaRequestDTO;
import Pool.hackaton.entity.Venta;
import Pool.hackaton.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Venta
 * Base URL: /api/ventas
 * ============================================================
 * RESUMEN DE ENDPOINTS:
 *   GET  /api/ventas       → listar todas las ventas
 *   GET  /api/ventas/{id}  → buscar venta por ID (incluye detalles)
 *   POST /api/ventas       → registrar nueva venta
 *
 * El POST recibe un VentaRequestDTO (no la entidad directamente)
 * porque necesita IDs simples, no objetos anidados completos.
 * ============================================================
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listar();
    }

    // Al buscar por ID, la respuesta incluye los detalles de la venta
    // gracias a la relación @OneToMany en la entidad Venta
    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarPorId(@PathVariable Integer id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/ventas
     *
     * Body esperado:
     * {
     *   "idCliente": 1,
     *   "idUsuario": 1,
     *   "detalles": [
     *     { "idProducto": 2, "cantidad": 1 },
     *     { "idProducto": 4, "cantidad": 2 }
     *   ]
     * }
     *
     * Respuesta 200: la venta creada con su ID y total calculado
     * Respuesta 400: si hay stock insuficiente u otro error de negocio
     */
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody VentaRequestDTO request) {
        try {
            Venta venta = ventaService.registrarVenta(request);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
