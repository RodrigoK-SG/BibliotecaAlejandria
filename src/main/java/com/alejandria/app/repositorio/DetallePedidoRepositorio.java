package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.DetallePedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Integer> {

    // Cantidad total de unidades vendidas en el mes
    @Query("SELECT COALESCE(SUM(dp.cantidad), 0) FROM DetallePedido dp JOIN dp.pedido p WHERE p.estadoActual != 'CANCELADO' AND p.fechaPedido BETWEEN :inicio AND :fin")
    Integer sumarLibrosVendidosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Top 5 Libros más vendidos para el nuevo gráfico de dona del Admin
    @Query("SELECT l.titulo, SUM(dp.cantidad) as total " +
           "FROM DetallePedido dp " +
           "JOIN dp.libro l " +
           "JOIN dp.pedido p " +
           "WHERE p.estadoActual != 'CANCELADO' AND p.fechaPedido BETWEEN :inicio AND :fin " +
           "GROUP BY l.id, l.titulo " +
           "ORDER BY total DESC")
    List<Object[]> obtenerTopLibrosVendidos(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, Pageable pageable);
}