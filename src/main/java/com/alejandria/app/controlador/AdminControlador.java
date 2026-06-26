package com.biblioteca.app.controlador;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.servicio.LibroServicio;
import com.biblioteca.app.servicio.UsuarioServicio; // Asegúrate de tener este servicio
import com.biblioteca.app.servicio.SucursalServicio;
import com.biblioteca.app.servicio.EditorialServicio;
import com.biblioteca.app.modelo.Libro;
import com.biblioteca.app.modelo.enums.FormatoLibro;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControlador {

    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;
    private final SucursalServicio sucursalServicio;
    private final EditorialServicio editorialServicio;

    @GetMapping({"", "/"})
    public String verCatalogo(Model model) {
        // Estadísticas Reales (Tus servicios deben tener un método count() o similar)
        // Si no tienes métodos contarActivos(), puedes usar .size() de tus listas por ahora
        model.addAttribute("totalLibrosActivos", libroServicio.listarTodosLosLibros().stream().filter(Libro::getActivo).count());
        model.addAttribute("totalUsuarios", usuarioServicio.listarTodos().size()); 
        model.addAttribute("totalSucursales", sucursalServicio.listarTodas().size());
        
        // Cargamos la lista dinámica de libros para la tabla
        model.addAttribute("libros", libroServicio.listarTodosLosLibros());
        
        return "administrador/catalogo"; 
    }

    @PostMapping("/editar")
    public String editarLibro(
            @RequestParam("id") Integer id,
            @RequestParam("titulo") String titulo,
            @RequestParam("formato") String formato,
            @RequestParam("precioVenta") BigDecimal precioVenta,
            @RequestParam("precioAlquiler") BigDecimal precioAlquiler,
            @RequestParam("editorial") String nombreEditorial,
            @RequestParam(value = "activo", defaultValue = "false") boolean activo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Buscamos el libro existente
            Libro libro = libroServicio.buscarPorId(id);
            
            // Actualizamos los campos permitidos
            libro.setTitulo(titulo);
            libro.setFormato(FormatoLibro.valueOf(formato));
            libro.setPrecioVentaActual(precioVenta);
            libro.setPrecioAlquilerActual(precioAlquiler);
            libro.setActivo(activo);
            
            // Verificamos y asignamos la editorial si el admin la cambió
            if (nombreEditorial != null && !nombreEditorial.trim().isEmpty()) {
                libro.setEditorial(editorialServicio.obtenerOAsignarDefault(nombreEditorial));
            }
            
            libroServicio.guardar(libro);
            redirectAttributes.addFlashAttribute("exito", "Libro actualizado correctamente.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el libro: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    @PostMapping("/desactivar")
    public String desactivarLibro(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Libro libro = libroServicio.buscarPorId(id);
            libro.setActivo(false); // Lo ocultamos del catálogo público
            libroServicio.guardar(libro);
            
            redirectAttributes.addFlashAttribute("exito", "El libro ha sido desactivado del catálogo público.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al desactivar el libro.");
        }
        
        return "redirect:/admin";
    }
}