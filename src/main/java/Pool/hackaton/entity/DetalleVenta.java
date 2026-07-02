package Pool.hackaton.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * ============================================================
 * ENTIDAD: DetalleVenta  →  tabla DETALLE_VENTA en SQL Server
 * ============================================================
 * Representa UN producto dentro de una venta.
 * Si una venta tiene 3 productos → 3 filas en esta tabla.
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO?
 * -------------------------------------------------------------
 * Ejemplo: agregar un campo "descuento"
 * 1. SQL:   ALTER TABLE DETALLE_VENTA ADD descuento DECIMAL(5,2) DEFAULT 0;
 * 2. Aquí:  @Column(name = "descuento", precision = 5, scale = 2)
 *           private BigDecimal descuento;
 * 3. En DetalleVentaDTO: agregar  private BigDecimal descuento;
 * 4. En VentaService: detalle.setDescuento(item.getDescuento());
 * ============================================================
 */
@Data
@Entity
@Table(name = "DETALLE_VENTA")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // FK hacia la venta padre (NO se expone en el JSON de salida para evitar recursión)
    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    // FK hacia el producto vendido
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // COLUMNA CALCULADA en SQL: subtotal AS (cantidad * precio_unitario) PERSISTED
    // insertable = false → JPA no la incluye en el INSERT
    // updatable  = false → JPA no la incluye en el UPDATE
    // SQL Server la calcula sola cuando se insertan los otros dos campos
    @Column(name = "subtotal", insertable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
