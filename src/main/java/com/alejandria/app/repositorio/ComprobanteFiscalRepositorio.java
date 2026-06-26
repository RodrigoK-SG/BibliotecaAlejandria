package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.ComprobanteFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ComprobanteFiscalRepositorio extends JpaRepository<ComprobanteFiscal, Integer> {
    Optional<ComprobanteFiscal> findByPedidoId(Integer pedidoId);
    Optional<ComprobanteFiscal> findBySerieAndNumero(String serie, String numero);
}