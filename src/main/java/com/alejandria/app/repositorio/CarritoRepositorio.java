package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByClienteId(Integer clienteId);
}