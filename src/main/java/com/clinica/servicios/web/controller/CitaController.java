package com.clinica.servicios.web.controller;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.domain.enums.EstadoCita;
import com.clinica.servicios.service.BusinessException;
import com.clinica.servicios.service.CitaService;
import com.clinica.servicios.service.HistorialService;
import com.clinica.servicios.service.MedicoService;
import com.clinica.servicios.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final HistorialService historialService;

    public CitaController(CitaService citaService,
                          PacienteService pacienteService,
                          MedicoService medicoService,
                          HistorialService historialService) {
        this.citaService = citaService;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.historialService = historialService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         @RequestParam(required = false) EstadoCita estado,
                         @RequestParam(required = false) Long medicoId,
                         Model model) {
        model.addAttribute("citas", citaService.listar(fecha, estado, medicoId));
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("estados", EstadoCita.values());
        model.addAttribute("fecha", fecha);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("medicoSeleccionado", medicoId);
        return "citas/list";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long pacienteId,
                        @RequestParam(required = false) Long medicoId,
                        Model model) {
        Cita cita = new Cita();
        cita.setPacienteSeleccionadoId(pacienteId);
        cita.setMedicoSeleccionadoId(medicoId);
        prepararFormulario(model, cita, false);
        return "citas/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("cita") Cita cita,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        return guardar(cita, result, model, redirectAttributes, false);
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Cita cita = citaService.obtener(id);
        HistorialMedico historial = historialService.buscarPorCita(id).orElse(null);
        model.addAttribute("cita", cita);
        model.addAttribute("historial", historial);
        return "citas/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Cita cita = citaService.obtener(id);
        cita.setPacienteSeleccionadoId(cita.getPaciente().getId());
        cita.setMedicoSeleccionadoId(cita.getMedico().getId());
        prepararFormulario(model, cita, true);
        return "citas/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("cita") Cita cita,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        cita.setId(id);
        return guardar(cita, result, model, redirectAttributes, true);
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoCita estado,
                                RedirectAttributes redirectAttributes) {
        try {
            citaService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("exito", "Estado de la cita actualizado a " + estado.getEtiqueta() + ".");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/citas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            citaService.cambiarEstado(id, EstadoCita.CANCELADA);
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/citas/" + id;
    }

    private String guardar(Cita cita,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           boolean edicion) {
        if (result.hasErrors()) {
            prepararFormulario(model, cita, edicion);
            return "citas/form";
        }
        try {
            citaService.guardar(cita);
            redirectAttributes.addFlashAttribute("exito",
                    edicion ? "Cita actualizada correctamente." : "Cita registrada correctamente.");
            return "redirect:/citas";
        } catch (BusinessException e) {
            result.reject("error.cita", e.getMessage());
            prepararFormulario(model, cita, edicion);
            return "citas/form";
        }
    }

    private void prepararFormulario(Model model, Cita cita, boolean edicion) {
        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("modoEdicion", edicion);
    }
}
