package Pool.hackaton.dto;

import lombok.Data;
import java.util.List;

/**
 * ============================================================
 * DTO: VentaRequestDTO
 * ============================================================
 * Representa el cuerpo JSON que el frontend envía al crear una venta.
 *
 * Ejemplo de JSON:
 * {
 *   "idCliente": 1,
 *   "idUsuario": 1,
 *   "detalles": [
 *     { "idProducto": 1, "cantidad": 2 },
 *     { "idProducto": 4, "cantidad": 1 }
 *   ]
 * }
 *
 * ¿CÓMO AGREGAR UN CAMPO EXTRA A LA VENTA?
 * -------------------------------------------------------------
 * Ejemplo: agregar "observacion" a la venta
 * 1. SQL:    ALTER TABLE VENTAS ADD observacion VARCHAR(255);
 * 2. En Venta.java:  private String observacion;
 * 3. Aquí:   private String observacion;
 * 4. En VentaService.registrarVenta():
 *            venta.setObservacion(request.getObservacion());
 * ============================================================
 */
@Data
public class VentaRequestDTO {

    private Integer idCliente;            // Quién compra
    private Integer idUsuario;            // Quién registra la venta
    private List<DetalleVentaDTO> detalles; // Lista de productos
}
