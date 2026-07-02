package Pool.hackaton.service;

import Pool.hackaton.dto.DetalleVentaDTO;
import Pool.hackaton.dto.VentaRequestDTO;
import Pool.hackaton.entity.*;
import Pool.hackaton.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * SERVICIO: Venta
 * ============================================================
 * La operación más importante: registrarVenta()
 *
 * @Transactional garantiza ATOMICIDAD:
 * Si cualquier paso falla (stock insuficiente, producto no existe,
 * error de BD, etc.), se revierten TODOS los cambios.
 * Es decir, o se guarda todo o no se guarda nada.
 *
 * ¿CÓMO AGREGAR LÓGICA EXTRA AL REGISTRAR UNA VENTA?
 * -------------------------------------------------------------
 * Ejemplo: enviar un correo de confirmación
 * → Agrega el servicio de correo con @RequiredArgsConstructor
 *   y llámalo al final de registrarVenta(), después del save().
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> buscarPorId(Integer id) {
        return ventaRepository.findById(id);
    }

    /**
     * REGISTRAR VENTA
     * Pasos en orden:
     *   1. Validar que cliente y usuario existan
     *   2. Crear la cabecera de la venta
     *   3. Por cada producto: verificar stock → descontar stock → crear detalle
     *   4. Calcular el total
     *   5. Guardar la venta (JPA guarda los detalles automáticamente por CascadeType.ALL)
     */
    @Transactional
    public Venta registrarVenta(VentaRequestDTO request) {

        // Paso 1: Buscar cliente y usuario. Si no existen lanza excepción
        // orElseThrow detiene la ejecución y el @Transactional revierte todo
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + request.getIdCliente()));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + request.getIdUsuario()));

        // Paso 2: Crear cabecera de venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now()); // Fecha actual del servidor Java
        venta.setTotal(BigDecimal.ZERO);     // Se actualiza en el paso 4

        // Paso 3: Procesar cada ítem del detalle
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVentaDTO item : request.getDetalles()) {

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getIdProducto()));

            // Validar stock suficiente antes de descontar
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para '" + producto.getNombre()
                        + "'. Disponible: " + producto.getStock()
                        + ", solicitado: " + item.getCantidad());
            }

            // Descontar stock del producto
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Crear el detalle con el precio actual del producto
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);                          // Referencia al padre
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());  // Precio al momento de la venta

            // Acumular al total: subtotal = precio × cantidad
            BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(item.getCantidad()));
            total = total.add(subtotal);

            detalles.add(detalle);
        }

        // Paso 4: Setear total y lista de detalles
        venta.setTotal(total);
        venta.setDetalles(detalles);

        // Paso 5: Guardar. CascadeType.ALL guarda los detalles automáticamente
        return ventaRepository.save(venta);
    }
}
