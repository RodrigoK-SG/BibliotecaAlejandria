package com.biblioteca.app.controlador;

import com.biblioteca.app.modelo.Rol;
import com.biblioteca.app.modelo.Usuario;
import com.biblioteca.app.servicio.RolServicio;
import com.biblioteca.app.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final RolServicio rolServicio;
    private final PasswordEncoder passwordEncoder; // Para encriptar las contraseñas

    @GetMapping
    public String verUsuarios(Model model) {
        // Obtenemos todos los usuarios excepto los CLIENTE_WEB
        model.addAttribute("usuarios", usuarioServicio.listarPersonalAdmin());
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
            // Encriptar la contraseña
            nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));
            nuevoUsuario.setActivo(activo);

            // Asignar el Rol
            Rol rol = rolServicio.buscarPorNombre(rolNombre);
            List<Rol> roles = new ArrayList<>();
            roles.add(rol);
            nuevoUsuario.setRoles(roles);

            usuarioServicio.guardar(nuevoUsuario);
            redirectAttributes.addFlashAttribute("exito", "Usuario registrado correctamente.");

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
            Usuario usuario = usuarioServicio.buscarPorId(id);
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setEmail(email);
            usuario.setActivo(activo);

            // Solo actualizar contraseña si se escribió una nueva
            if (password != null && !password.trim().isEmpty()) {
                usuario.setPasswordHash(passwordEncoder.encode(password));
            }

            // Actualizar el Rol
            Rol rol = rolServicio.buscarPorNombre(rolNombre);
            List<Rol> roles = new ArrayList<>();
            roles.add(rol);
            usuario.setRoles(roles);

            usuarioServicio.guardar(usuario);
            redirectAttributes.addFlashAttribute("exito", "Perfil de usuario actualizado.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/desactivar")
    public String desactivarUsuario(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioServicio.buscarPorId(id);
            usuario.setActivo(false); // Solo se cambia el estado, no se elimina
            usuarioServicio.guardar(usuario);
            redirectAttributes.addFlashAttribute("exito", "Usuario desactivado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}