package com.alejandria.app.servicio;

import com.alejandria.app.modelo.MovimientosInventario;
import com.alejandria.app.modelo.InventarioVenta;
import com.alejandria.app.modelo.enums.TipoMovimiento;
import com.alejandria.app.repositorio.MovimientosInventarioRepositorio;
import com.alejandria.app.repositorio.InventarioVentaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioServicio {

    private final MovimientosInventarioRepositorio movimientosRepositorio;
    private final InventarioVentaRepositorio inventarioRepository;

    public List<MovimientosInventario> listarTodos() {
        return movimientosRepositorio.findAll();
    }

    public List<MovimientosInventario> buscarPorLibro(Integer libroId) {
        return movimientosRepositorio.findByLibroIdOrderByFechaMovimientoDesc(libroId);
    }

    @Transactional
    public void registrarMovimiento(MovimientosInventario movimiento) {
        Integer libroId = movimiento.getLibro().getId();
        int cantidad = movimiento.getCantidad();
        TipoMovimiento tipo = movimiento.getTipoMovimiento();

        boolean esEntrada = tipo == TipoMovimiento.INGRESO_PROVEEDOR || tipo == TipoMovimiento.DEVOLUCION || tipo == TipoMovimiento.AJUSTE_MANUAL;
        boolean esSalida = tipo == TipoMovimiento.VENTA_ONLINE || tipo == TipoMovimiento.MERMA;

        InventarioVenta inventario = inventarioRepository.findById(libroId)
                .orElseGet(() -> {
                    if (esSalida) {
                        throw new RuntimeException("Error: No se puede registrar una salida de inventario para un libro sin registro inicial de stock.");
                    }
                    InventarioVenta nuevoInv = new InventarioVenta();
                    nuevoInv.setLibro(movimiento.getLibro());
                    nuevoInv.setCantidadDisponible(0);
                    nuevoInv.setCantidadReservada(0);
                    return nuevoInv;
                });

        if (esEntrada) {
            inventario.setCantidadDisponible(inventario.getCantidadDisponible() + cantidad);
        } else if (esSalida) {
            if (inventario.getCantidadDisponible() < cantidad) {
                throw new RuntimeException("Stock físico insuficiente en el almacén. Disponible: " + inventario.getCantidadDisponible());
            }
            inventario.setCantidadDisponible(inventario.getCantidadDisponible() - cantidad);
        }

        inventarioRepository.save(inventario);
        movimientosRepositorio.save(movimiento);
    }
}