package com.alejandria.app.filtro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RutaControlador {

    @GetMapping("/")
    public String redirigirAlCatalogo() {
        // Esto le dice al navegador: "Oye, muévete automáticamente a /catalogo"
        return "redirect:/catalogo";
    }
}