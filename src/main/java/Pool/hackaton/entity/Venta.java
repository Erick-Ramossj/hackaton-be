package Pool.hackaton.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * ENTIDAD: Venta  →  tabla VENTAS en SQL Server
 * ============================================================
 * Es la CABECERA de una venta. Cada venta tiene:
 *   - Un cliente que compra
 *   - Un usuario (vendedor) que registra la venta
 *   - Una lista de DetalleVenta (los productos comprados)
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO?
 * -------------------------------------------------------------
 * Ejemplo: agregar un campo "observacion"
 * 1. SQL:   ALTER TABLE VENTAS ADD observacion VARCHAR(255);
 * 2. Aquí:  @Column(name = "observacion", length = 255)
 *           private String observacion;
 * 3. En VentaRequestDTO: agregar  private String observacion;
 *    y en VentaService.registrarVenta():  venta.setObservacion(request.getObservacion());
 * ============================================================
 */
@Data
@Entity
@Table(name = "VENTAS")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    // Fecha y hora del momento en que se registró la venta
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    // ---- RELACIONES (Foreign Keys) ----
    // @ManyToOne: muchas ventas pueden pertenecer a un mismo cliente
    // @JoinColumn: indica qué columna es la FK en la tabla VENTAS
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // ---- DETALLE (relación padre-hijo) ----
    // @OneToMany: una venta tiene muchos detalles
    // mappedBy = "venta": le dice que el campo "venta" en DetalleVenta es el dueño de la relación
    // cascade = ALL: al guardar/eliminar la venta, se guardan/eliminan sus detalles también
    // orphanRemoval = true: si se quita un detalle de la lista, se borra de la BD
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;
}
