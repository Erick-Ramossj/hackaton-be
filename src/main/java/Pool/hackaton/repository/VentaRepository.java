package Pool.hackaton.repository;

import Pool.hackaton.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Venta
 * ============================================================
 * Para navegar por relaciones en el nombre del mÃ©todo se usa
 * el nombre del campo en la entidad + el campo de la entidad relacionada.
 *
 * Ejemplo: findByClienteIdCliente
 *   "Cliente"   â†’ campo "cliente" en Venta (la relaciÃ³n @ManyToOne)
 *   "IdCliente" â†’ campo "idCliente" en la entidad Cliente
 * ============================================================
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Todas las ventas de un cliente especÃ­fico
    List<Venta> findByClienteIdCliente(Integer idCliente);
}

