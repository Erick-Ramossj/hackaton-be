package Pool.hackaton.repository;

import Pool.hackaton.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Producto
 * ============================================================
 * JpaRepository<Producto, Integer> ya incluye gratis:
 *   save(p)          → INSERT o UPDATE
 *   findById(id)     → SELECT WHERE id = ?
 *   findAll()        → SELECT *
 *   deleteById(id)   → DELETE WHERE id = ?
 *   existsById(id)   → SELECT COUNT WHERE id = ?
 *
 * Los métodos adicionales usan convención de nombres de Spring:
 * findBy + NombreCampo + Condicion
 *
 * ¿CÓMO AGREGAR UN NUEVO MÉTODO DE BÚSQUEDA?
 * -------------------------------------------------------------
 * Ejemplo: buscar por marca (si agregaste ese campo)
 *   List<Producto> findByMarcaAndEstadoTrue(String marca);
 *
 * Spring genera el SQL automáticamente. No hace falta escribirlo.
 * ============================================================
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // SELECT * FROM PRODUCTOS WHERE estado = 1
    List<Producto> findByEstadoTrue();

    // SELECT * FROM PRODUCTOS WHERE categoria = ? AND estado = 1
    List<Producto> findByCategoriaAndEstadoTrue(String categoria);
}
