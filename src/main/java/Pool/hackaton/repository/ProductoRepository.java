package Pool.hackaton.repository;

import Pool.hackaton.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Producto
 * ============================================================
 * JpaRepository<Producto, Integer> ya incluye gratis:
 *   save(p)          â†’ INSERT o UPDATE
 *   findById(id)     â†’ SELECT WHERE id = ?
 *   findAll()        â†’ SELECT *
 *   deleteById(id)   â†’ DELETE WHERE id = ?
 *   existsById(id)   â†’ SELECT COUNT WHERE id = ?
 *
 * Los mÃ©todos adicionales usan convenciÃ³n de nombres de Spring:
 * findBy + NombreCampo + Condicion
 *
 * Â¿CÃ“MO AGREGAR UN NUEVO MÃ‰TODO DE BÃšSQUEDA?
 * -------------------------------------------------------------
 * Ejemplo: buscar por marca (si agregaste ese campo)
 *   List<Producto> findByMarcaAndEstadoTrue(String marca);
 *
 * Spring genera el SQL automÃ¡ticamente. No hace falta escribirlo.
 * ============================================================
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // SELECT * FROM PRODUCTOS WHERE estado = 1
    List<Producto> findByEstadoTrue();

    List<Producto> findByEstadoFalse();

    // SELECT * FROM PRODUCTOS WHERE categoria = ? AND estado = 1
    List<Producto> findByCategoriaAndEstadoTrue(String categoria);
}

