package Pool.hackaton.rest;

import Pool.hackaton.model.Producto;
import Pool.hackaton.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/inactivos")
    public List<Producto> listarInactivos() {
        return productoService.listarInactivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Integer id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.buscarPorId(id).map(existente -> {
            producto.setIdProducto(id);
            return ResponseEntity.ok(productoService.guardar(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = productoService.eliminar(id);
        return ok ? ResponseEntity.ok("Producto desactivado")
                  : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/{id}/restaurar", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> restaurar(@PathVariable Integer id) {
        boolean ok = productoService.restaurar(id);
        return ok ? ResponseEntity.ok("Producto restaurado")
                  : ResponseEntity.notFound().build();
    }

    // ---- IMPORTACION ----
    // consumes = MULTIPART_FORM_DATA_VALUE hace que Swagger muestre el campo de archivo
    @Operation(summary = "Importar productos desde CSV",
               description = "Formato: nombre,descripcion,categoria,precio,stock (una fila por producto)")
    @PostMapping(value = "/importar/csv",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarCSV(
            @Parameter(description = "Archivo CSV", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Importar productos desde Excel (.xlsx)",
               description = "Cabecera en fila 1: nombre | descripcion | categoria | precio | stock")
    @PostMapping(value = "/importar/excel",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarExcel(
            @Parameter(description = "Archivo Excel .xlsx", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeExcel(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde Excel.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ---- EXPORTACION ----
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
