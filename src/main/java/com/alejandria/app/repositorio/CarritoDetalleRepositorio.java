package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.CarritoDetalle;
import com.alejandria.app.modelo.CarritoDetalleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarritoDetalleRepositorio extends JpaRepository<CarritoDetalle, CarritoDetalleId> {
    // Hereda las operaciones CRUD para el detalle utilizando el ID compuesto de llaves foráneas.
	@Modifying
	@Query("DELETE FROM CarritoDetalle c WHERE c.carrito.id = :carritoId AND c.libro.id = :libroId")
	void deleteByCarritoIdAndLibroId(@Param("carritoId") Integer carritoId,
	                                 @Param("libroId") Integer libroId);
}