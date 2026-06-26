package com.biblioteca.app.controlador;

import com.biblioteca.app.modelo.Sucursal;
import com.biblioteca.app.servicio.SucursalServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sucursales")
@RequiredArgsConstructor
public class AdminSucursalControlador {

    private final SucursalServicio sucursalServicio;

    @GetMapping
    public String verSucursales(Model model) {
        model.addAttribute("sucursales", sucursalServicio.listarTodas());
        return "administrador/sucursales"; 
    }

    @PostMapping("/nuevo")
    public String registrarSucursal(@RequestParam("nombreSede") String nombre,
                                    @RequestParam("direccion") String direccion,
                                    @RequestParam("telefono") String telefono,
                                    @RequestParam(value = "servicioBiblioteca", defaultValue = "false") boolean tieneBiblioteca,
                                    RedirectAttributes redirectAttributes) {
        try {
            Sucursal nueva = new Sucursal();
            nueva.setNombre(nombre);
            nueva.setDireccion(direccion);
            nueva.setTelefono(telefono);
            nueva.setTieneBiblioteca(tieneBiblioteca);
            nueva.setActivo(true);

            sucursalServicio.guardar(nueva);
            redirectAttributes.addFlashAttribute("exito", "Sucursal registrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la sucursal: " + e.getMessage());
        }
        return "redirect:/admin/sucursales";
    }

    @PostMapping("/editar")
    public String editarSucursal(@RequestParam("id") Integer id,
                                 @RequestParam("nombreSede") String nombre,
                                 @RequestParam("direccion") String direccion,
                                 @RequestParam("telefono") String telefono,
                                 @RequestParam(value = "servicioBiblioteca", defaultValue = "false") boolean tieneBiblioteca,
                                 RedirectAttributes redirectAttributes) {
        try {
            Sucursal existente = sucursalServicio.buscarPorId(id);
            existente.setNombre(nombre);
            existente.setDireccion(direccion);
            existente.setTelefono(telefono);
            existente.setTieneBiblioteca(tieneBiblioteca);

            sucursalServicio.guardar(existente);
            redirectAttributes.addFlashAttribute("exito", "Sucursal actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la sucursal.");
        }
        return "redirect:/admin/sucursales";
    }

    @PostMapping("/desactivar")
    public String desactivarSucursal(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Sucursal existente = sucursalServicio.buscarPorId(id);
            existente.setActivo(false);
            sucursalServicio.guardar(existente);
            
            redirectAttributes.addFlashAttribute("exito", "Sucursal desactivada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar la sucursal.");
        }
        return "redirect:/admin/sucursales";
    }
}