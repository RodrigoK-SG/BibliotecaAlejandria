package com.alejandria.app.servicio;

import com.alejandria.app.modelo.*;
import com.alejandria.app.modelo.enums.*;
import com.alejandria.app.repositorio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServicio {

    private final CarritoRepositorio carritoRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final InventarioVentaRepositorio inventarioRepository;
    private final MovimientosInventarioRepositorio movimientosRepository;
    private final PagoRepositorio pagoRepositorio;
    private final ComprobanteFiscalRepositorio comprobanteRepositorio;
    private final CarritoServicio carritoServicio;

    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepositorio.findById(id);
    }

    public List<Pedido> listarPedidosPorCliente(Integer clienteId) {
        return pedidoRepositorio.findByClienteIdOrderByFechaPedidoDesc(clienteId);
    }

    @Transactional
    public Pedido procesarVentaCheckout(Integer clienteId, DireccionEnvio direccion, MetodoPago metodoPago, TipoComprobante tipoComprobante) {
        
        // 1. Buscamos el carrito del cliente
        Carrito carrito = carritoRepositorio.findByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("El cliente no cuenta con un carrito activo para procesar."));

        if (carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("No se puede realizar una compra si el carrito está vacío.");
        }

        // 2. Armamos la cabecera del Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(carrito.getCliente());
        pedido.setDireccionEnvio(direccion);
        pedido.setCanalVenta(CanalVenta.ONLINE);
        pedido.setEstadoActual(EstadoPedido.PAGADO);
        
        BigDecimal totalAcumulado = BigDecimal.ZERO;

        // 3. Procesamos cada libro dentro del carrito
        for (CarritoDetalle item : carrito.getDetalles()) {
            Libro libro = item.getLibro();
            int cantRequerida = item.getCantidad();

            // 3.1 Verificamos y restamos el Stock Físico
            InventarioVenta inventario = inventarioRepository.findById(libro.getId())
                    .orElseThrow(() -> new RuntimeException("No existe registro logístico para el libro: " + libro.getTitulo()));

            if (inventario.getCantidadDisponible() < cantRequerida) {
                throw new RuntimeException("Stock insuficiente en tienda para el título: " + libro.getTitulo() + ". Disponible: " + inventario.getCantidadDisponible());
            }

            inventario.setCantidadDisponible(inventario.getCantidadDisponible() - cantRequerida);
            inventarioRepository.save(inventario);

            // 3.2 Registramos el movimiento en el historial (Kardex)
            MovimientosInventario mov = new MovimientosInventario();
            mov.setLibro(libro);
            mov.setCantidad(cantRequerida);
            mov.setTipoMovimiento(TipoMovimiento.VENTA_ONLINE);
            mov.setDescripcion("Despacho automático por compra web - Orden Online");
            movimientosRepository.save(mov);

            // 3.3 Creamos el detalle del pedido
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setLibro(libro);
            detalle.setCantidad(cantRequerida);
            detalle.setPrecioUnitario(libro.getPrecioVentaActual());
            
            BigDecimal subtotal = libro.getPrecioVentaActual().multiply(new BigDecimal(cantRequerida));
            detalle.setSubtotal(subtotal);
            totalAcumulado = totalAcumulado.add(subtotal);

            pedido.getDetalles().add(detalle);
        }

        // 4. Guardamos el pedido total
        pedido.setTotal(totalAcumulado);
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

        // 5. Registramos el Pago simulado
        Pago pago = new Pago();
        pago.setPedido(pedidoGuardado);
        pago.setMetodoPago(metodoPago);
        pago.setEstadoPago(EstadoPago.APROBADO);
        pago.setMonto(totalAcumulado);
        pago.setReferenciaPago("PAY-WEB-" + System.currentTimeMillis());
        pagoRepositorio.save(pago);

        // 6. Emitimos la Boleta / Factura
        ComprobanteFiscal comprobante = new ComprobanteFiscal();
        comprobante.setPedido(pedidoGuardado);
        comprobante.setTipoComprobante(tipoComprobante);
        comprobante.setSerie(tipoComprobante == TipoComprobante.BOLETA ? "B001" : "F001");
        comprobante.setNumero(String.valueOf(System.currentTimeMillis()).substring(4)); // Número aleatorio simulado
        
        BigDecimal impuesto = totalAcumulado.multiply(new BigDecimal("0.18")); // IGV del 18%
        comprobante.setMontoImpuesto(impuesto);
        comprobante.setMontoTotal(totalAcumulado);
        comprobanteRepositorio.save(comprobante);

        // 7. Vaciamos el carrito porque la compra ya se hizo
        carritoServicio.limpiarCarrito(carrito);

        return pedidoGuardado;
    }
    
    public Optional<Pedido> findById(Integer id) {
        return pedidoRepositorio.findById(id);
    }
}