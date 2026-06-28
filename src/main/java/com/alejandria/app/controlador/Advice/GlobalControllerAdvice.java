package com.alejandria.app.controlador.Advice;

import com.alejandria.app.modelo.Cliente;
import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.repositorio.ClienteRepositorio;
import com.alejandria.app.servicio.CarritoServicio;
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
    private final ClienteRepositorio clienteRepositorio;
    private final CarritoServicio carritoServicio;

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
    
    @ModelAttribute("cantidadCarrito")
    public Integer cargarCantidadCarrito() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            Usuario usuario = usuarioServicio.buscarPorEmail(auth.getName()).orElse(null);
            if (usuario != null) {
                Cliente cliente = clienteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);
                if (cliente != null) {
                    // Contamos cuántos libros diferentes hay en el carrito
                    return carritoServicio.obtenerPorCliente(cliente.getId())
                                          .map(c -> c.getDetalles().stream().mapToInt(d -> d.getCantidad()).sum())
                                          .orElse(0);
                }
            }
        }
        return 0;
    }
}