package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DireccionEnvioRepositorio extends JpaRepository<DireccionEnvio, Integer> {
    List<DireccionEnvio> findByClienteId(Integer clienteId);
}