package com.alejandria.app.servicio;

import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.modelo.Rol;
import com.alejandria.app.repositorio.UsuarioRepositorio;
import com.alejandria.app.repositorio.RolRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya se encuentra registrado.");
        }
        
        Rol rol = rolRepositorio.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("El rol especificado no existe: " + nombreRol));
        
        usuario.getRoles().add(rol);
        usuario.setActivo(true);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario actualizarEstado(Integer id, boolean activo) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(activo);
        return usuarioRepositorio.save(usuario);
    }
}