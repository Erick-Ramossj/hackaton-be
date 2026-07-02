package Pool.hackaton.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidad CLIENTES — hereda los 4 campos de auditoría de Auditable.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "CLIENTES")
public class Cliente extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
