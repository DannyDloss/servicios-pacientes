package com.clinica.servicios.web.controller;

import com.clinica.servicios.domain.Medico;
import com.clinica.servicios.service.BusinessException;
import com.clinica.servicios.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("medicos", medicoService.listar(q));
        model.addAttribute("q", q);
        return "medicos/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("modoEdicion", false);
        return "medicos/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("medico") Medico medico,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        return guardar(medico, result, model, redirectAttributes, false);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.obtener(id));
        model.addAttribute("modoEdicion", true);
        return "medicos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("medico") Medico medico,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        medico.setId(id);
        return guardar(medico, result, model, redirectAttributes, true);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicoService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Medico eliminado correctamente.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/medicos";
    }

    private String guardar(Medico medico,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           boolean edicion) {
        if (result.hasErrors()) {
            model.addAttribute("modoEdicion", edicion);
            return "medicos/form";
        }
        try {
            medicoService.guardar(medico);
            redirectAttributes.addFlashAttribute("exito",
                    edicion ? "Medico actualizado correctamente." : "Medico registrado correctamente.");
            return "redirect:/medicos";
        } catch (BusinessException e) {
            result.rejectValue("colegiatura", "error.colegiatura", e.getMessage());
            model.addAttribute("modoEdicion", edicion);
            return "medicos/form";
        }
    }
}
