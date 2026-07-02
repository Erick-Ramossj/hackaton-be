package Pool.hackaton.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * Entidad PRODUCTOS — hereda los 4 campos de auditoría de Auditable.
 * Es la entidad MAESTRA principal del sistema.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "PRODUCTOS")
public class Producto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
