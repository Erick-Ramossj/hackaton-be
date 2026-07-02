package Pool.hackaton.rest;

import Pool.hackaton.model.Producto;
import Pool.hackaton.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/productos -> lista productos ACTIVOS
    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    // GET /api/productos/inactivos -> lista productos INACTIVOS (eliminados logicamente)
    @GetMapping("/inactivos")
    public List<Producto> listarInactivos() {
        return productoService.listarInactivos();
    }

    // GET /api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Integer id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/productos -> crear
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    // PUT /api/productos/{id} -> actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.buscarPorId(id).map(existente -> {
            producto.setIdProducto(id);
            return ResponseEntity.ok(productoService.guardar(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/productos/{id} -> baja logica: estado=false, deleted_at=now
    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = productoService.eliminar(id);
        return ok ? ResponseEntity.ok("Producto desactivado")
                  : ResponseEntity.notFound().build();
    }

    // PUT /api/productos/{id}/restaurar -> restauracion logica: estado=true, restored_at=now
    @PutMapping(value = "/{id}/restaurar", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> restaurar(@PathVariable Integer id) {
        boolean ok = productoService.restaurar(id);
        return ok ? ResponseEntity.ok("Producto restaurado")
                  : ResponseEntity.notFound().build();
    }

    // POST /api/productos/importar/csv
    @PostMapping(value = "/importar/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarCSV(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // POST /api/productos/importar/excel
    @PostMapping(value = "/importar/excel", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarExcel(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeExcel(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde Excel.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET /api/productos/exportar/excel
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
