package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutorRepositorio extends JpaRepository<Autor, Integer> {
    List<String> findByNombreContainingIgnoreCase(String nombre);
}