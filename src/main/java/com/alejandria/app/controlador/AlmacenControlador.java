package com.alejandria.app.controlador;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alejandria.app.servicio.LibroServicio;
import com.biblioteca.app.servicio.MovimientosInventarioServicio;
import com.biblioteca.app.servicio.EditorialServicio;
import com.biblioteca.app.servicio.InventarioVentaServicio;
import com.biblioteca.app.servicio.SucursalServicio;
import com.biblioteca.app.modelo.MovimientosInventario;
import com.biblioteca.app.modelo.Sucursal;
import com.biblioteca.app.modelo.enums.FormatoLibro;
import com.biblioteca.app.modelo.Libro;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/almacen")
@RequiredArgsConstructor
public class AlmacenControlador {

    private final LibroServicio libroServicio;
    private final MovimientosInventarioServicio movimientosServicio;
    private final InventarioVentaServicio inventarioServicio;
    private final SucursalServicio sucursalServicio; 
    private final EditorialServicio editorialServicio; // AÑADIDO PARA LA EDITORIAL

    @GetMapping({"", "/"})
    public String inicioAlmacen() {
        return "redirect:/almacen/movimientos";
    }
    
    // --- VISTA DE MOVIMIENTOS ---
    @GetMapping("/movimientos")
    public String verMovimientos(Model model) {
        // Listas para los selects del modal
        model.addAttribute("libros", libroServicio.listarTodosLosLibros());
        model.addAttribute("sucursales", sucursalServicio.listarTodas()); 
        
        // Evitar sobreescribir si ya existe un error en el modal
        if (!model.containsAttribute("nuevoMovimiento")) {
            model.addAttribute("nuevoMovimiento", new MovimientosInventario());
        }
        
        model.addAttribute("listaMovimientos", movimientosServicio.listarTodosLosMovimientos());
        
        return "almacenero/movimientos";
    }

    // --- GUARDAR MOVIMIENTO DESDE EL MODAL ---
 // --- GUARDAR MOVIMIENTO DESDE EL MODAL ---
    @PostMapping("/movimientos/registrar")
    public String registrarMovimiento(
            @ModelAttribute("nuevoMovimiento") MovimientosInventario movimiento,
            @RequestParam("sucursalId") Integer sucursalId,
            @RequestParam(value = "esNuevoLibro", defaultValue = "false") boolean esNuevoLibro,
            @RequestParam(value = "nuevoIsbn", required = false) String nuevoIsbn,
            @RequestParam(value = "nuevoTitulo", required = false) String nuevoTitulo,
            @RequestParam(value = "nuevaImagen", required = false) String nuevaImagen,
            @RequestParam(value = "nuevasPaginas", required = false, defaultValue = "100") Integer nuevasPaginas,
            @RequestParam(value = "nuevaEditorial", required = false) String nuevaEditorial, 
            @RequestParam(value = "formato", required = false) String formato, // <-- CAPTURAMOS EL FORMATO
            @RequestParam(value = "nuevoPrecio", required = false, defaultValue = "0.00") BigDecimal nuevoPrecio, // <-- CAPTURAMOS EL PRECIO
            RedirectAttributes redirectAttributes) { 
        
        try {
            // 1. Asignamos la Sucursal seleccionada en el modal
            Sucursal sucursal = sucursalServicio.buscarPorId(sucursalId);
            movimiento.setSucursal(sucursal); 

            // 2. Lógica para Libros Nuevos vs Existentes
            if (esNuevoLibro && nuevoIsbn != null && !nuevoIsbn.trim().isEmpty()) {
                
                Libro nuevoLibro = new Libro();
                nuevoLibro.setIsbn(nuevoIsbn);
                nuevoLibro.setTitulo(nuevoTitulo != null && !nuevoTitulo.isEmpty() ? nuevoTitulo : "Libro Autogenerado (" + nuevoIsbn + ")");
                nuevoLibro.setImagenPortada(nuevaImagen);
                // Aseguramos que las páginas siempre sean mayor a 0 para que la BD no explote
                Integer paginasValidas = (nuevasPaginas != null && nuevasPaginas > 0) ? nuevasPaginas : 1;
                nuevoLibro.setPaginas(paginasValidas);                
                // ASIGNAMOS EL PRECIO Y FORMATO QUE VIENE DEL HTML
                nuevoLibro.setPrecioVentaActual(nuevoPrecio); 
                nuevoLibro.setFormato(FormatoLibro.valueOf(formato != null ? formato : "TAPA_BLANDA"));                
                nuevoLibro.setActivo(true);
                nuevoLibro.setEditorial(editorialServicio.obtenerOAsignarDefault(nuevaEditorial));
                
                Libro libroGuardado = libroServicio.guardar(nuevoLibro);
                movimiento.setLibro(libroGuardado); 
                
            } else {
                if (movimiento.getLibro() != null && movimiento.getLibro().getId() != null) {
                    Libro libroExistente = libroServicio.buscarPorId(movimiento.getLibro().getId());
                    movimiento.setLibro(libroExistente);
                } else {
                    throw new RuntimeException("Error: Debe seleccionar un libro válido de la lista.");
                }
            }

            // 3. Guardamos el movimiento
            movimientosServicio.registrarMovimiento(movimiento);
            redirectAttributes.addFlashAttribute("exito", "Movimiento de inventario registrado correctamente.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("nuevoMovimiento", movimiento);
        }
        
        return "redirect:/almacen/movimientos";
    }

    // --- VISTA DE STOCK ---
 // ... Todo lo de arriba queda igual ...

    // --- VISTA DE STOCK ---
    @GetMapping("/stock")
    public String verStock(Model model) {
        // AQUÍ EL CAMBIO: Llamamos al servicio de inventario, no al de libros
        model.addAttribute("listaStock", inventarioServicio.listarStockGeneral());
        return "almacenero/stock";
    }

    // ... Todo lo de abajo queda igual ...

    // --- VISTA DE ALERTAS ---
 // --- VISTA DE ALERTAS ---
    @GetMapping("/alertar")
    public String verAlertas(Model model) {
        // Mandamos la lista filtrada a la vista
        model.addAttribute("listaAlertas", inventarioServicio.listarAlertasStock());
        return "almacenero/alertar";
    }
}