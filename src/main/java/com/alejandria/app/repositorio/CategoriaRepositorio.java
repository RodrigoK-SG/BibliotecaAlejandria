package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByNombre(String nombre);
}