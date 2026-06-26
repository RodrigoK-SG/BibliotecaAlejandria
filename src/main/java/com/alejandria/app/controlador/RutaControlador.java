package com.alejandria.app.controlador; // Es mejor tenerlo aquí

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RutaControlador {

    @GetMapping("/")
    public String redirigirATienda() {
        // Esto le dice al navegador: "Oye, muévete automáticamente a /tienda"
        return "redirect:/tienda";
    }
}