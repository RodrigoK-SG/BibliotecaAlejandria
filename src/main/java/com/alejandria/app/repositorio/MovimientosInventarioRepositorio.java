package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.MovimientosInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientosInventarioRepositorio extends JpaRepository<MovimientosInventario, Integer> {
    List<MovimientosInventario> findByLibroIdOrderByFechaMovimientoDesc(Integer libroId);
}