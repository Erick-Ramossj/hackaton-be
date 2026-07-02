package Pool.hackaton.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ============================================================
 * ENTIDAD: Cliente  →  tabla CLIENTES en SQL Server
 * ============================================================
 * ¿CÓMO AGREGAR UN NUEVO CAMPO?
 * -------------------------------------------------------------
 * 1. SQL:   ALTER TABLE CLIENTES ADD direccion VARCHAR(200);
 * 2. Aquí:  @Column(name = "direccion", length = 200)
 *           private String direccion;
 * ============================================================
 */
@Data
@Entity
@Table(name = "CLIENTES")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // CHAR(8): exactamente 8 caracteres (DNI peruano)
    // unique = true → equivale a UNIQUE en SQL
    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    // Baja lógica: true = activo, false = inactivo
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    /*
     * ---- EJEMPLO: agregar campo dirección ----
     *
     * SQL:   ALTER TABLE CLIENTES ADD direccion VARCHAR(200);
     *
     * Aquí:
     *   @Column(name = "direccion", length = 200)
     *   private String direccion;
     */
}
