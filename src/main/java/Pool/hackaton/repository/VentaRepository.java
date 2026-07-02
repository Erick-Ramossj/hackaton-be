package Pool.hackaton.repository;

import Pool.hackaton.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Venta
 * ============================================================
 * Para navegar por relaciones en el nombre del método se usa
 * el nombre del campo en la entidad + el campo de la entidad relacionada.
 *
 * Ejemplo: findByClienteIdCliente
 *   "Cliente"   → campo "cliente" en Venta (la relación @ManyToOne)
 *   "IdCliente" → campo "idCliente" en la entidad Cliente
 * ============================================================
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Todas las ventas de un cliente específico
    List<Venta> findByClienteIdCliente(Integer idCliente);
}
