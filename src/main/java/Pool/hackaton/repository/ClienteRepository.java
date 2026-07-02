package Pool.hackaton.repository;

import Pool.hackaton.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Cliente
 * ============================================================
 * Â¿CÃ“MO AGREGAR UN NUEVO MÃ‰TODO?
 * -------------------------------------------------------------
 * Ejemplo: buscar clientes por nombre (bÃºsqueda parcial)
 *   List<Cliente> findByNombreContainingIgnoreCase(String nombre);
 *   â†’ genera: SELECT * FROM CLIENTES WHERE nombre LIKE '%?%'
 * ============================================================
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByEstadoTrue();

    List<Cliente> findByEstadoFalse();

    boolean existsByDni(String dni);

    boolean existsByDniAndIdClienteNot(String dni, Integer idCliente);
}

