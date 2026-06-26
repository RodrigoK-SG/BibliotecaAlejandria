package com.alejandria.app.controlador;

import com.alejandria.app.modelo.Rol;
import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.repositorio.RolRepositorio;
import com.alejandria.app.repositorio.UsuarioRepositorio;
import com.alejandria.app.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final UsuarioRepositorio usuarioRepository;
    private final RolRepositorio rolRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String verUsuarios(Model model) {
        // Filtramos para mostrar únicamente al personal administrativo (Admin y Almaceneros)
        // Excluimos las cuentas de clientes de la tienda pública
        List<Usuario> personalAdministrativo = usuarioServicio.listarTodos().stream()
                .filter(u -> u.getRoles().stream().noneMatch(r -> r.getNombre().equals("CLIENTE_WEB")))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", personalAdministrativo);
        return "administrador/usuarios"; 
    }

    @PostMapping("/nuevo")
    public String registrarUsuario(@RequestParam("nombreCompleto") String nombreCompleto,
                                   @RequestParam("password") String password,
                                   @RequestParam("email") String email,
                                   @RequestParam("rol") String rolNombre,
                                   @RequestParam(value = "activo", defaultValue = "false") boolean activo,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));
            nuevoUsuario.setActivo(activo);

            // CORREGIDO: Buscamos el rol y lo agregamos directamente al Set con .add()
            Rol rol = rolRepository.findByNombre(rolNombre)
                    .orElseThrow(() -> new RuntimeException("El rol especificado no existe en el sistema."));
            nuevoUsuario.getRoles().add(rol);

            usuarioRepository.save(nuevoUsuario);
            redirectAttributes.addFlashAttribute("exito", "Usuario del sistema registrado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/editar")
    public String editarUsuario(@RequestParam("id") Integer id,
                                @RequestParam("nombreCompleto") String nombreCompleto,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam("email") String email,
                                @RequestParam("rol") String rolNombre,
                                @RequestParam(value = "activo", defaultValue = "false") boolean activo,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioServicio.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
                    
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setEmail(email);
            usuario.setActivo(activo);

            if (password != null && !password.trim().isEmpty()) {
                usuario.setPasswordHash(passwordEncoder.encode(password));
            }

            // CORREGIDO: Limpiamos el Set anterior y añadimos el nuevo rol asignado
            Rol rol = rolRepository.findByNombre(rolNombre)
                    .orElseThrow(() -> new RuntimeException("El rol especificado no existe."));
            usuario.getRoles().clear();
            usuario.getRoles().add(rol);

            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("exito", "Perfil de usuario actualizado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/desactivar")
    public String desactivarUsuario(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // Reutilizamos la lógica del servicio para suspender el acceso de un usuario
            usuarioServicio.actualizarEstado(id, false);
            redirectAttributes.addFlashAttribute("exito", "El usuario ha sido suspendido del sistema.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}