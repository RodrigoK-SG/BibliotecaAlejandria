package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepositorio extends JpaRepository<Pago, Integer> {
    List<Pago> findByPedidoId(Integer pedidoId);
}