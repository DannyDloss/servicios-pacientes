package com.clinica.servicios.web.controller;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.service.BusinessException;
import com.clinica.servicios.service.CitaService;
import com.clinica.servicios.service.HistorialService;
import com.clinica.servicios.service.MedicoService;
import com.clinica.servicios.service.PacienteService;
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

import java.time.LocalDateTime;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialService historialService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final CitaService citaService;

    public HistorialController(HistorialService historialService,
                               PacienteService pacienteService,
                               MedicoService medicoService,
                               CitaService citaService) {
        this.historialService = historialService;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.citaService = citaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("historiales", historialService.listar(q));
        model.addAttribute("q", q);
        return "historial/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long citaId,
                        @RequestParam(required = false) Long pacienteId,
                        @RequestParam(required = false) Long medicoId,
                        Model model) {
        HistorialMedico historial = new HistorialMedico();
        historial.setFechaConsulta(LocalDateTime.now());
        Cita cita = null;

        if (citaId != null) {
            cita = citaService.obtener(citaId);
            historial.setCitaSeleccionadaId(cita.getId());
            historial.setPacienteSeleccionadoId(cita.getPaciente().getId());
            historial.setMedicoSeleccionadoId(cita.getMedico().getId());
            historial.setMotivoConsulta(cita.getMotivo());
        } else {
            historial.setPacienteSeleccionadoId(pacienteId);
            historial.setMedicoSeleccionadoId(medicoId);
        }

        prepararFormulario(model, historial, cita, false);
        return "historial/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("historial") HistorialMedico historial,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        return guardar(historial, result, model, redirectAttributes, false);
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("historial", historialService.obtener(id));
        return "historial/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        HistorialMedico historial = historialService.obtener(id);
        Cita cita = historial.getCita();
        if (cita != null) {
            historial.setCitaSeleccionadaId(cita.getId());
            historial.setPacienteSeleccionadoId(cita.getPaciente().getId());
            historial.setMedicoSeleccionadoId(cita.getMedico().getId());
        }
        prepararFormulario(model, historial, cita, true);
        return "historial/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("historial") HistorialMedico historial,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        historial.setId(id);
        return guardar(historial, result, model, redirectAttributes, true);
    }

    private String guardar(HistorialMedico historial,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           boolean edicion) {
        Cita cita = resolverCita(historial);
        if (result.hasErrors()) {
            prepararFormulario(model, historial, cita, edicion);
            return "historial/form";
        }
        try {
            HistorialMedico guardado = historialService.guardar(historial);
            redirectAttributes.addFlashAttribute("exito",
                    edicion ? "Historial actualizado correctamente." : "Historial registrado correctamente.");
            return "redirect:/historial/" + guardado.getId();
        } catch (BusinessException e) {
            result.reject("error.historial", e.getMessage());
            prepararFormulario(model, historial, cita, edicion);
            return "historial/form";
        }
    }

    private Cita resolverCita(HistorialMedico historial) {
        if (historial.getCitaSeleccionadaId() == null) {
            return null;
        }
        try {
            return citaService.obtener(historial.getCitaSeleccionadaId());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void prepararFormulario(Model model, HistorialMedico historial, Cita cita, boolean edicion) {
        model.addAttribute("historial", historial);
        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("modoEdicion", edicion);
    }
}
