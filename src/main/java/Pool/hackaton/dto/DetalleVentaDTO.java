package Pool.hackaton.dto;

import lombok.Data;

/**
 * ============================================================
 * DTO: DetalleVentaDTO
 * ============================================================
 * DTO = Data Transfer Object.
 * Es un objeto simple que representa los datos que llegan
 * del frontend para un ítem del detalle de la venta.
 *
 * ¿Por qué no usamos directamente la entidad DetalleVenta?
 * Porque DetalleVenta tiene una relación con Venta, y si lo
 * usáramos directamente tendríamos que enviar el objeto Venta
 * completo desde el frontend, lo cual es innecesario.
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO AL DETALLE?
 * -------------------------------------------------------------
 * Ejemplo: agregar descuento por ítem
 * 1. SQL:    ALTER TABLE DETALLE_VENTA ADD descuento DECIMAL(5,2) DEFAULT 0;
 * 2. En DetalleVenta.java:  private BigDecimal descuento;
 * 3. Aquí:   private BigDecimal descuento;
 * 4. En VentaService: detalle.setDescuento(item.getDescuento());
 * ============================================================
 */
@Data
public class DetalleVentaDTO {

    private Integer idProducto;   // ID del producto a vender
    private Integer cantidad;     // Cantidad solicitada
}
