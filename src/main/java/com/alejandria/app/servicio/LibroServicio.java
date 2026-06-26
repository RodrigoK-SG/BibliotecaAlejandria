package com.alejandria.app.servicio;

import com.alejandria.app.modelo.Libro;
import com.alejandria.app.repositorio.LibroRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibroServicio {

    private final LibroRepositorio libroRepositorio;

    public List<Libro> listarTodos() {
        return libroRepositorio.findAll();
    }

    public Optional<Libro> buscarPorId(Integer id) {
        return libroRepositorio.findById(id);
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepositorio.findByIsbn(isbn);
    }

    public Optional<Libro> buscarPorSlug(String slug) {
        return libroRepositorio.findBySlug(slug);
    }

    @Transactional
    public Libro guardarLibro(Libro libro) {
        if (libro.getId() == null && libroRepositorio.findByIsbn(libro.getIsbn()).isPresent()) {
            throw new RuntimeException("Ya existe un libro con el mismo ISBN registrado.");
        }
        
        if (libro.getTitulo() != null) {
            String slug = libro.getTitulo().toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-");
            libro.setSlug(slug);
        }
        
        return libroRepositorio.save(libro);
    }

    @Transactional
    public void eliminarPorId(Integer id) {
        Libro libro = libroRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("El libro que intenta eliminar no existe."));
        libro.setActivo(false);
        libroRepositorio.save(libro);
    }
}