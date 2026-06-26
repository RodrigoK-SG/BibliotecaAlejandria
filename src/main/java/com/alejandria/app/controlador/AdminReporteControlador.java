package com.biblioteca.app.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.servicio.ReporteServicio;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
@RequiredArgsConstructor
public class AdminReporteControlador {

    private final ReporteServicio reporteServicio;

    @GetMapping
    public String verReportes(
            @RequestParam(value = "mes", required = false) String mes, 
            Model model) {
        
        // Llamamos a toda la lógica de negocio
        Map<String, Object> datos = reporteServicio.generarReporteMensual(mes);
        
        // Inyectamos todo el mapa en el modelo para que Thymeleaf lo lea
        model.addAllAttributes(datos);
        
        return "administrador/reportes"; 
    }
}