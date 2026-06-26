package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Pedido;
import com.alejandria.app.modelo.enums.CanalVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
    
    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(Integer clienteId);

    // Ingresos totales del mes (Reporte del Admin)
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estadoActual <> 'CANCELADO' AND p.fechaPedido BETWEEN :inicio AND :fin")
    Double sumarIngresosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Ingresos mensuales segregados por canal (Físico vs Online para gráficos de barras)
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.canalVenta = :canal AND p.estadoActual != 'CANCELADO' AND p.fechaPedido BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosPorCanalYFecha(@Param("canal") CanalVenta canal, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}