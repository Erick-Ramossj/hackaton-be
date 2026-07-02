package Pool.hackaton.rest;

import Pool.hackaton.model.Cliente;
import Pool.hackaton.service.ClienteService;
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
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/inactivos")
    public List<Cliente> listarInactivos() {
        return clienteService.listarInactivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.ok(clienteService.guardar(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        return clienteService.buscarPorId(id).map(existente -> {
            cliente.setIdCliente(id);
            return ResponseEntity.ok(clienteService.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        boolean ok = clienteService.eliminar(id);
        return ok ? ResponseEntity.ok("Cliente desactivado")
                  : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/{id}/restaurar", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> restaurar(@PathVariable Integer id) {
        boolean ok = clienteService.restaurar(id);
        return ok ? ResponseEntity.ok("Cliente restaurado")
                  : ResponseEntity.notFound().build();
    }

    // ---- IMPORTACION ----
    @Operation(summary = "Importar clientes desde CSV",
               description = "Formato: nombre,dni,telefono,email (una fila por cliente)")
    @PostMapping(value = "/importar/csv",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarCSV(
            @Parameter(description = "Archivo CSV", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = clienteService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " clientes desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Importar clientes desde Excel (.xlsx)",
               description = "Cabecera en fila 1: nombre | dni | telefono | email")
    @PostMapping(value = "/importar/excel",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importarExcel(
            @Parameter(description = "Archivo Excel .xlsx", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            int total = clienteService.importarDesdeExcel(archivo);
            return ResponseEntity.ok("Se importaron " + total + " clientes desde Excel.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ---- EXPORTACION ----
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] datos = clienteService.exportarExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.xlsx")
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
            byte[] datos = clienteService.exportarPDF();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
