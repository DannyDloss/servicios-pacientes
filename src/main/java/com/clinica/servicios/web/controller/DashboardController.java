package com.clinica.servicios.web.controller;

import com.clinica.servicios.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalPacientes", dashboardService.totalPacientes());
        model.addAttribute("totalMedicos", dashboardService.totalMedicos());
        model.addAttribute("citasHoy", dashboardService.citasHoy());
        model.addAttribute("citasProgramadas", dashboardService.citasProgramadas());
        model.addAttribute("recetasEmitidas", dashboardService.recetasEmitidas());
        model.addAttribute("proximasCitas", dashboardService.proximasCitas());
        model.addAttribute("historialesRecientes", dashboardService.historialesRecientes());
        return "dashboard/index";
    }
}
