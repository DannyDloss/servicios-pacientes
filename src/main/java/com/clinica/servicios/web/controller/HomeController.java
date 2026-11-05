package com.clinica.servicios.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Muestra la pagina de inicio del sistema.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "home";
    }
}
