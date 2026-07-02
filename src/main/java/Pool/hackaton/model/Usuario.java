package Pool.hackaton.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidad USUARIOS — hereda los 4 campos de auditoría de Auditable.
 * La lógica de cuándo llenar cada campo está en UsuarioService.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "USUARIOS")
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
