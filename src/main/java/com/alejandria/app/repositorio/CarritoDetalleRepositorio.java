package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.CarritoDetalle;
import com.alejandria.app.modelo.CarritoDetalleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoDetalleRepositorio extends JpaRepository<CarritoDetalle, CarritoDetalleId> {
    // Hereda las operaciones CRUD para el detalle utilizando el ID compuesto de llaves foráneas.
}