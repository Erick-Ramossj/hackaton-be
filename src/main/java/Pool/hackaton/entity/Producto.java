package Pool.hackaton.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * ============================================================
 * ENTIDAD: Producto  →  tabla PRODUCTOS en SQL Server
 * ============================================================
 * Cada campo de esta clase representa una columna de la tabla.
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO?
 * -------------------------------------------------------------
 * 1. En SQL Server (hackaton.sql):
 *       ALTER TABLE PRODUCTOS ADD marca VARCHAR(50);
 *
 * 2. Aquí abajo, agregar el campo con su @Column:
 *       @Column(name = "marca", length = 50)
 *       private String marca;
 *
 * 3. No necesitas tocar Repository ni Service para que el campo
 *    aparezca en el JSON. JPA lo maneja automáticamente.
 *
 * 4. Si quieres que sea obligatorio en el JSON de entrada,
 *    agrega  nullable = false  en el @Column.
 * ============================================================
 */
@Data                          // Lombok: genera getters, setters, toString, equals, hashCode
@Entity                        // Le dice a JPA que esta clase es una tabla
@Table(name = "PRODUCTOS")     // Nombre exacto de la tabla en la BD
public class Producto {

    // ---- CLAVE PRIMARIA ----
    // IDENTITY(1,1) en SQL = autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    // ---- CAMPOS BÁSICOS ----
    // nullable = false  →  NOT NULL en la BD
    // length            →  tamaño del VARCHAR
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;         // Puede ser null

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    // DECIMAL(10,2): hasta 10 dígitos en total, 2 decimales
    // precision = dígitos totales, scale = decimales
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    // ---- BAJA LÓGICA ----
    // En lugar de DELETE físico, se cambia estado a false.
    // true = activo, false = inactivo (no aparece en las listas)
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    /*
     * ---- EJEMPLO: así agregarías un campo nuevo ----
     *
     * En SQL:
     *   ALTER TABLE PRODUCTOS ADD marca VARCHAR(50);
     *
     * Aquí:
     *   @Column(name = "marca", length = 50)
     *   private String marca;
     *
     * Eso es todo. Al hacer GET /api/productos ya aparecerá "marca" en el JSON.
     */
}
