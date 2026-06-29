package com.alejandria.app.controlador;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alejandria.app.servicio.LibroServicio;
import com.alejandria.app.servicio.MovimientoInventarioServicio;
import com.alejandria.app.servicio.InventarioServicio;
import com.alejandria.app.servicio.UsuarioServicio;
import com.alejandria.app.repositorio.EditorialRepositorio;

import com.alejandria.app.modelo.MovimientosInventario;
import com.alejandria.app.modelo.Usuario;
import com.alejandria.app.modelo.Editorial;
import com.alejandria.app.modelo.enums.FormatoLibro;
import com.alejandria.app.modelo.Libro;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/almacen")
@RequiredArgsConstructor
public class AlmacenControlador {

    private final LibroServicio libroServicio;
    private final MovimientoInventarioServicio movimientoServicio;
    private final InventarioServicio inventarioServicio;
    private final EditorialRepositorio editorialRepositorio; 
    private final UsuarioServicio usuarioServicio; // Para rastrear quién hace el movimiento

    @GetMapping({"", "/"})
    public String inicioAlmacen() {
        return "redirect:/almacen/movimientos";
    }
    
    // ==========================================
    // --- VISTA DE MOVIMIENTOS ---
    // ==========================================
    @GetMapping("/movimientos")
    public String verMovimientos(Model model) {
        model.addAttribute("libros", libroServicio.listarTodos());
        
        // Si no existe 'nuevoMovimiento' en el modelo (por ejemplo, al entrar por primera vez),
        // creamos uno nuevo vacío.
        if (!model.containsAttribute("nuevoMovimiento")) {
            model.addAttribute("nuevoMovimiento", new MovimientosInventario());
        }
        
        model.addAttribute("listaMovimientos", movimientoServicio.listarTodos());
        
        return "almacenero/movimientos";
    }

    // ==========================================
    // --- GUARDAR MOVIMIENTO DESDE EL MODAL ---
    // ==========================================
    @PostMapping("/movimientos/registrar")
    public String registrarMovimiento(
            Principal principal,
            @ModelAttribute("nuevoMovimiento") MovimientosInventario movimiento,
            @RequestParam(value = "esNuevoLibro", defaultValue = "false") boolean esNuevoLibro,
            @RequestParam(value = "nuevoIsbn", required = false) String nuevoIsbn,
            @RequestParam(value = "nuevoTitulo", required = false) String nuevoTitulo,
            @RequestParam(value = "nuevaImagen", required = false) String nuevaImagen,
            @RequestParam(value = "nuevasPaginas", required = false, defaultValue = "100") Integer nuevasPaginas,
            @RequestParam(value = "nuevaEditorial", required = false) String nuevaEditorial, 
            @RequestParam(value = "nuevaSinopsis", required = false) String nuevaSinopsis, // <-- NUEVO CAMPO
            @RequestParam(value = "formato", required = false) String formato, 
            @RequestParam(value = "nuevoPrecio", required = false, defaultValue = "0.00") BigDecimal nuevoPrecio,
            RedirectAttributes redirectAttributes) { 
        
        try {
            // 1. Asignamos al operador
            if (principal != null) {
                Usuario operador = usuarioServicio.buscarPorEmail(principal.getName()).orElse(null);
                movimiento.setUsuario(operador);
            }

            // 2. Lógica para Libros Nuevos vs Existentes
            if (esNuevoLibro && nuevoIsbn != null && !nuevoIsbn.trim().isEmpty()) {
                
                Libro nuevoLibro = new Libro();
                nuevoLibro.setIsbn(nuevoIsbn);
                nuevoLibro.setTitulo(nuevoTitulo != null && !nuevoTitulo.isEmpty() ? nuevoTitulo : "Libro Autogenerado (" + nuevoIsbn + ")");
                
                // === MAGIA DE LA IMAGEN AQUÍ ===
                // Si la API mandó una imagen la usamos, si no, ARMAMOS EL LINK AUTOMÁTICAMENTE con el ISBN
                String urlPortada = (nuevaImagen != null && !nuevaImagen.trim().isEmpty()) 
                        ? nuevaImagen 
                        : "https://covers.openlibrary.org/b/isbn/" + nuevoIsbn.trim() + "-L.jpg";
                
                nuevoLibro.setImagenPortada(urlPortada);
                // ===============================

                nuevoLibro.setSinopsis(nuevaSinopsis);
                nuevoLibro.setPaginas((nuevasPaginas != null && nuevasPaginas > 0) ? nuevasPaginas : 1);                
                nuevoLibro.setPrecioVentaActual(nuevoPrecio); 
                nuevoLibro.setFormato(FormatoLibro.valueOf(formato != null ? formato : "TAPA_BLANDA"));                
                nuevoLibro.setActivo(true);
                
                // Buscar o crear editorial
                if (nuevaEditorial != null && !nuevaEditorial.trim().isEmpty()) {
                    Editorial editorial = editorialRepositorio.findByNombre(nuevaEditorial)
                            .orElseGet(() -> {
                                Editorial nuevaEd = new Editorial();
                                nuevaEd.setNombre(nuevaEditorial);
                                return editorialRepositorio.save(nuevaEd);
                            });
                    nuevoLibro.setEditorial(editorial);
                } else {
                    throw new RuntimeException("Debe especificar una editorial para el nuevo libro.");
                }
                
                Libro libroGuardado = libroServicio.guardarLibro(nuevoLibro);
                movimiento.setLibro(libroGuardado); 
                
            } else {
                if (movimiento.getLibro() != null && movimiento.getLibro().getId() != null) {
                    Libro libroExistente = libroServicio.buscarPorId(movimiento.getLibro().getId())
                            .orElseThrow(() -> new RuntimeException("El libro seleccionado no existe."));
                    movimiento.setLibro(libroExistente);
                } else {
                    throw new RuntimeException("Error: Debe seleccionar un libro válido de la lista.");
                }
            }

            // 3. Guardamos el movimiento
            movimientoServicio.registrarMovimiento(movimiento);
            redirectAttributes.addFlashAttribute("exito", "Movimiento registrado. Libro y portada guardados con éxito.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("nuevoMovimiento", movimiento);
        }
        
        return "redirect:/almacen/movimientos";
    }

    // ==========================================
    // --- VISTA DE STOCK (INVENTARIO GENERAL) ---
    // ==========================================
    @GetMapping("/stock")
    public String verStock(Model model) {
        model.addAttribute("listaStock", inventarioServicio.obtenerTodoElInventario());
        return "almacenero/stock";
    }

    // ==========================================
    // --- VISTA DE ALERTAS (STOCK CRÍTICO) ---
    // ==========================================
    @GetMapping("/alertar")
    public String verAlertas(Model model) {
        model.addAttribute("listaAlertas", inventarioServicio.obtenerStockCritico());
        return "almacenero/alertar";
    }
}