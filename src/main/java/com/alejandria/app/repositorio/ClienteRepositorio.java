package com.alejandria.app.repositorio;

import com.alejandria.app.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    Optional<Cliente> findByUsuarioId(Integer usuarioId);
}