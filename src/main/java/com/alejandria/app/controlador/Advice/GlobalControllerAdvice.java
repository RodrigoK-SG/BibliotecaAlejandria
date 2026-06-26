package com.alejandria.app.controlador.Advice;

import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UsuarioServicio usuarioServicio;

    // Este método se ejecutará automáticamente antes de cargar cualquier página HTML
    @ModelAttribute("usuarioLogueado")
    public Usuario cargarUsuarioGlobal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verificamos si hay alguien logueado (y que no sea un usuario anónimo de Spring)
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // auth.getName() devuelve el "username" (en tu caso, el email usado en el login)
            String emailLogueado = auth.getName(); 
            
            // Buscamos el objeto Usuario completo, y usamos .orElse(null) para extraerlo del Optional
            return usuarioServicio.buscarPorEmail(emailLogueado).orElse(null); 
        }
        
        return null; // Si no hay nadie logueado, Thymeleaf simplemente recibirá un null
    }
}