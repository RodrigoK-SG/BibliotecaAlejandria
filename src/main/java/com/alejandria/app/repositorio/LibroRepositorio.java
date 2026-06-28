package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepositorio extends JpaRepository<Libro, Integer> {
    Optional<Libro> findByIsbn(String isbn);
    Optional<Libro> findBySlug(String slug);
    List<Libro> findByTituloContainingIgnoreCaseOrIsbnContainingIgnoreCase(String titulo, String isbn);
}