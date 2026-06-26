package com.alejandria.app.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alejandria.app.servicio.ReporteServicio;
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
        
        // El servicio procesa las consultas nativas matemáticas y retorna el mapa de datos
        Map<String, Object> datos = reporteServicio.generarReporteMensual(mes);
        
        // Inyectamos todas las llaves del mapa directamente para que Thymeleaf las pinte
        model.addAllAttributes(datos);
        
        return "administrador/reportes"; 
    }
}