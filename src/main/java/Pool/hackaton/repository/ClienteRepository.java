package Pool.hackaton.repository;

import Pool.hackaton.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Cliente
 * ============================================================
 * ¿CÓMO AGREGAR UN NUEVO MÉTODO?
 * -------------------------------------------------------------
 * Ejemplo: buscar clientes por nombre (búsqueda parcial)
 *   List<Cliente> findByNombreContainingIgnoreCase(String nombre);
 *   → genera: SELECT * FROM CLIENTES WHERE nombre LIKE '%?%'
 * ============================================================
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Solo clientes activos
    List<Cliente> findByEstadoTrue();

    // Verifica si ya existe un cliente con ese DNI (para evitar duplicados)
    boolean existsByDni(String dni);

    // Verifica duplicado de DNI excluyendo al propio cliente (para validar en edición)
    boolean existsByDniAndIdClienteNot(String dni, Integer idCliente);
}
