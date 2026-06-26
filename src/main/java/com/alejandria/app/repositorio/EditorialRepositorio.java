package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EditorialRepositorio extends JpaRepository<Editorial, Integer> {
    Optional<Editorial> findByNombre(String nombre);
}