package com.alejandria.app.controlador;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alejandria.app.servicio.LibroServicio;
import com.alejandria.app.servicio.UsuarioServicio;
import com.alejandria.app.modelo.Libro;
import com.alejandria.app.modelo.Editorial;
import com.alejandria.app.modelo.enums.FormatoLibro;
import com.alejandria.app.repositorio.EditorialRepositorio;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControlador {

    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;
    private final EditorialRepositorio editorialRepositorio; // Usamos el repositorio directamente para simplificar

    @GetMapping({"", "/"})
    public String verCatalogo(Model model) {
        // Estadísticas calculadas dinámicamente desde el flujo del MVP
        model.addAttribute("totalLibrosActivos", libroServicio.listarTodos().stream().filter(Libro::getActivo).count());
        model.addAttribute("totalUsuarios", usuarioServicio.listarTodos().size()); 
        
        // Cargamos la lista completa de libros para la tabla de gestión
        model.addAttribute("libros", libroServicio.listarTodos());
        
        return "administrador/catalogo"; 
    }

    @PostMapping("/editar")
    public String editarLibro(
            @RequestParam("id") Integer id,
            @RequestParam("titulo") String titulo,
            @RequestParam("formato") String formato,
            @RequestParam("precioVenta") BigDecimal precioVenta,
            @RequestParam("editorial") String nombreEditorial,
            @RequestParam(value = "activo", defaultValue = "false") boolean activo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // .buscarPorId ahora devuelve un Optional, lo extraemos de forma segura
            Libro libro = libroServicio.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("El libro especificado no existe."));
            
            // Actualizamos los campos comerciales puros (Sin alquiler)
            libro.setTitulo(titulo);
            libro.setFormato(FormatoLibro.valueOf(formato));
            libro.setPrecioVentaActual(precioVenta);
            libro.setActivo(activo);
            
            // Si la editorial no existe, la creamos al vuelo para mantener la integridad
            if (nombreEditorial != null && !nombreEditorial.trim().isEmpty()) {
                Editorial editorial = editorialRepositorio.findByNombre(nombreEditorial)
                        .orElseGet(() -> {
                            Editorial nuevaEd = new Editorial();
                            nuevaEd.setNombre(nombreEditorial);
                            return editorialRepositorio.save(nuevaEd);
                        });
                libro.setEditorial(editorial);
            }
            
            libroServicio.guardarLibro(libro);
            redirectAttributes.addFlashAttribute("exito", "Libro actualizado correctamente.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el libro: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    @PostMapping("/desactivar")
    public String desactivarLibro(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // El servicio ya tiene un método dedicado para dar de baja lógica
            libroServicio.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "El libro ha sido desactivado del catálogo público.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al desactivar el libro: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }
}