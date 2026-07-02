package Pool.hackaton.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ============================================================
 * CLASE BASE DE AUDITORÍA
 * ============================================================
 * Todas las entidades maestras heredan de esta clase para
 * tener los 4 campos de auditoría requeridos.
 *
 * @MappedSuperclass: JPA incluye estos campos en cada tabla
 * hija sin crear una tabla separada para esta clase.
 *
 * Campos:
 *   created_at  → se llena al crear (en el Service)
 *   updated_at  → se llena al actualizar (en el Service)
 *   deleted_at  → se llena al hacer baja lógica (estado=false)
 *   restored_at → se llena al restaurar (estado=true de nuevo)
 * ============================================================
 */
@Data
@MappedSuperclass
public abstract class Auditable {

    // Fecha en que se creó el registro — nunca se modifica después
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Fecha de la última modificación
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Fecha en que se hizo baja lógica (estado = false)
    // null si el registro está activo
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Fecha en que se restauró el registro (estado = true de nuevo)
    // null si nunca fue restaurado
    @Column(name = "restored_at")
    private LocalDateTime restoredAt;
}
