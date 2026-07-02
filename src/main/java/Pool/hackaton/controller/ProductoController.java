package Pool.hackaton.controller;

import Pool.hackaton.entity.Producto;
import Pool.hackaton.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Producto
 * Base URL: /api/productos
 * ============================================================
 * Cada @GetMapping, @PostMapping, etc. es un endpoint REST.
 * @CrossOrigin permite que Angular (puerto 4200) consuma la API.
 *
 * RESUMEN DE ENDPOINTS:
 *   GET    /api/productos              → listar activos
 *   GET    /api/productos/{id}         → buscar por ID
 *   POST   /api/productos              → crear
 *   PUT    /api/productos/{id}         → actualizar
 *   DELETE /api/productos/{id}         → baja lógica
 *   POST   /api/productos/importar/csv    → importar CSV
 *   POST   /api/productos/importar/excel  → importar Excel
 *   GET    /api/productos/exportar/excel  → descargar Excel
 *   GET    /api/productos/exportar/pdf    → descargar PDF
 *
 * ¿CÓMO AGREGAR UN NUEVO ENDPOINT?
 * -------------------------------------------------------------
 * Ejemplo: buscar por categoría
 *   @GetMapping("/categoria/{cat}")
 *   public List<Producto> porCategoria(@PathVariable String cat) {
 *       return productoService.buscarPorCategoria(cat);
 *       // También debes agregar ese método en el Service
 *   }
 * ============================================================
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/productos
    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    // GET /api/productos/{id}
    // ResponseEntity permite controlar el código HTTP de la respuesta
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Integer id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)                    // 200 OK si lo encontró
                .orElse(ResponseEntity.notFound().build()); // 404 si no existe
    }

    // POST /api/productos
    // @RequestBody convierte el JSON del body al objeto Producto
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    // PUT /api/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.buscarPorId(id).map(existente -> {
            producto.setIdProducto(id); // Asegura que actualice el registro correcto
            return ResponseEntity.ok(productoService.guardar(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/productos/{id} → baja lógica (no borra de BD)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = productoService.eliminar(id);
        return ok ? ResponseEntity.ok("Producto desactivado")
                  : ResponseEntity.notFound().build();
    }

    // ---- IMPORTACIÓN ----

    // POST /api/productos/importar/csv
    // @RequestParam("archivo"): el frontend envía el archivo con la clave "archivo"
    @PostMapping("/importar/csv")
    public ResponseEntity<String> importarCSV(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // POST /api/productos/importar/excel
    @PostMapping("/importar/excel")
    public ResponseEntity<String> importarExcel(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeExcel(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde Excel.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ---- EXPORTACIÓN ----

    // GET /api/productos/exportar/excel
    // El header Content-Disposition le dice al navegador que descargue el archivo
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] datos = productoService.exportarExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /api/productos/exportar/pdf
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPDF() {
        try {
            byte[] datos = productoService.exportarPDF();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
