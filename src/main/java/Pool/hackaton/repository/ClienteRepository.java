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

    // Solo clientes activos
    List<Cliente> findByEstadoTrue();

    // Verifica si ya existe un cliente con ese DNI (para evitar duplicados)
    boolean existsByDni(String dni);

    // Verifica duplicado de DNI excluyendo al propio cliente (para validar en ediciÃ³n)
    boolean existsByDniAndIdClienteNot(String dni, Integer idCliente);
}

