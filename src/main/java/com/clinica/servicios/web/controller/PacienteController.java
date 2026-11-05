package com.clinica.servicios.web.controller;

import com.clinica.servicios.domain.Paciente;
import com.clinica.servicios.service.BusinessException;
import com.clinica.servicios.service.CitaService;
import com.clinica.servicios.service.HistorialService;
import com.clinica.servicios.service.PacienteService;
import com.clinica.servicios.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas para listar, registrar, editar y eliminar pacientes.
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final CitaService citaService;
    private final HistorialService historialService;
    private final RecetaService recetaService;

    public PacienteController(PacienteService pacienteService,
                              CitaService citaService,
                              HistorialService historialService,
                              RecetaService recetaService) {
        this.pacienteService = pacienteService;
        this.citaService = citaService;
        this.historialService = historialService;
        this.recetaService = recetaService;
    }

    // Lista todos los pacientes.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteService.listar());
        return "pacientes/list";
    }

    // Muestra el formulario para un paciente nuevo.
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("modoEdicion", false);
        return "pacientes/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("paciente") Paciente paciente,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        return guardar(paciente, result, model, redirectAttributes, false);
    }

    // Muestra la ficha del paciente con sus citas, historial y recetas.
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.obtener(id);
        model.addAttribute("paciente", paciente);
        model.addAttribute("citas", citaService.listarPorPaciente(id));
        model.addAttribute("historiales", historialService.listarPorPaciente(id));
        model.addAttribute("recetas", recetaService.listarPorPaciente(id));
        return "pacientes/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", pacienteService.obtener(id));
        model.addAttribute("modoEdicion", true);
        return "pacientes/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("paciente") Paciente paciente,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        paciente.setId(id);
        return guardar(paciente, result, model, redirectAttributes, true);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Paciente eliminado correctamente.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pacientes";
    }

    // Metodo comun para crear y actualizar.
    private String guardar(Paciente paciente,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           boolean edicion) {
        if (result.hasErrors()) {
            model.addAttribute("modoEdicion", edicion);
            return "pacientes/form";
        }
        try {
            pacienteService.guardar(paciente);
            redirectAttributes.addFlashAttribute("exito",
                    edicion ? "Paciente actualizado correctamente." : "Paciente registrado correctamente.");
            return "redirect:/pacientes";
        } catch (BusinessException e) {
            result.rejectValue("documento", "error.documento", e.getMessage());
            model.addAttribute("modoEdicion", edicion);
            return "pacientes/form";
        }
    }
}
