package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.InventarioVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InventarioVentaRepositorio extends JpaRepository<InventarioVenta, Integer> {
    
    // Alerta de stock crítico para el almacenero (Mide la tienda única)
    @Query("SELECT i FROM InventarioVenta i WHERE i.cantidadDisponible <= i.stockMinimo")
    List<InventarioVenta> buscarStockCritico();
}