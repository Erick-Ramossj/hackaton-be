package Pool.hackaton.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ============================================================
 * ENTIDAD: Usuario  →  tabla USUARIOS en SQL Server
 * ============================================================
 * Roles válidos: ADMIN, VENDEDOR
 * (definidos con CHECK en la BD)
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO?
 * -------------------------------------------------------------
 * 1. SQL:   ALTER TABLE USUARIOS ADD apellido VARCHAR(100);
 * 2. Aquí:  @Column(name = "apellido", length = 100)
 *           private String apellido;
 * ============================================================
 */
@Data
@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // "usuario" es el nombre de login (único en la BD)
    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    // NOTA: en producción real se guardaría hasheado (BCrypt, etc.)
    // Para la hackaton se guarda en texto plano por simplicidad
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    // Valores posibles: "ADMIN" o "VENDEDOR"
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    // Baja lógica
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
