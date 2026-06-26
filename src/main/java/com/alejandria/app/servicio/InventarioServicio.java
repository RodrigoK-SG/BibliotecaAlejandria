package com.alejandria.app.servicio;

import com.alejandria.app.modelo.InventarioVenta;
import com.alejandria.app.repositorio.InventarioVentaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventarioServicio {

    private final InventarioVentaRepositorio inventarioVentaRepositorio;

    public List<InventarioVenta> obtenerTodoElInventario() {
        return inventarioVentaRepositorio.findAll();
    }

    public Optional<InventarioVenta> buscarPorLibroId(Integer libroId) {
        return inventarioVentaRepositorio.findById(libroId);
    }

    public List<InventarioVenta> obtenerStockCritico() {
        return inventarioVentaRepositorio.buscarStockCritico();
    }

    @Transactional
    public InventarioVenta actualizarLimitesStock(Integer libroId, Integer min, Integer max) {
        InventarioVenta inventario = inventarioVentaRepositorio.findById(libroId)
                .orElseThrow(() -> new RuntimeException("No se encontró registro de inventario para el libro."));
        
        if (min != null && min >= 0) inventario.setStockMinimo(min);
        if (max != null && max >= min) inventario.setStockMaximo(max);
        
        return inventarioVentaRepositorio.save(inventario);
    }
}