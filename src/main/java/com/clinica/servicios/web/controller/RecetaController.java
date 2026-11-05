package com.clinica.servicios.web.controller;

import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.domain.Receta;
import com.clinica.servicios.domain.RecetaItem;
import com.clinica.servicios.service.BusinessException;
import com.clinica.servicios.service.HistorialService;
import com.clinica.servicios.service.RecetaPdfService;
import com.clinica.servicios.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/recetas")
public class RecetaController {

    private final RecetaService recetaService;
    private final HistorialService historialService;
    private final RecetaPdfService recetaPdfService;

    public RecetaController(RecetaService recetaService,
                            HistorialService historialService,
                            RecetaPdfService recetaPdfService) {
        this.recetaService = recetaService;
        this.historialService = historialService;
        this.recetaPdfService = recetaPdfService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("recetas", recetaService.listar(q));
        model.addAttribute("q", q);
        return "recetas/list";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long historialId, Model model) {
        Receta receta = new Receta();
        receta.setFechaEmision(LocalDateTime.now());
        receta.addItem(new RecetaItem());
        if (historialId != null) {
            receta.setHistorialSeleccionadoId(historialId);
        }
        prepararFormulario(model, receta);
        return "recetas/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("receta") Receta receta,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepararFormulario(model, receta);
            return "recetas/form";
        }
        try {
            Receta creada = recetaService.crear(receta);
            redirectAttributes.addFlashAttribute("exito", "Receta emitida correctamente.");
            return "redirect:/recetas/" + creada.getId();
        } catch (BusinessException e) {
            result.reject("error.receta", e.getMessage());
            prepararFormulario(model, receta);
            return "recetas/form";
        }
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("receta", recetaService.obtener(id));
        return "recetas/detail";
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            recetaService.anular(id);
            redirectAttributes.addFlashAttribute("exito", "Receta anulada correctamente.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recetas/" + id;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Receta receta = recetaService.obtener(id);
        byte[] contenido = recetaPdfService.generar(id);
        String nombre = "receta-" + receta.getCodigo() + ".pdf";
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(nombre, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(contenido.length)
                .body(contenido);
    }

    private void prepararFormulario(Model model, Receta receta) {
        if (receta.getItems() == null || receta.getItems().isEmpty()) {
            receta.addItem(new RecetaItem());
        }
        model.addAttribute("receta", receta);
        model.addAttribute("historiales", historialService.listar(null));
        if (receta.getHistorialSeleccionadoId() != null) {
            HistorialMedico historial = historialService.obtener(receta.getHistorialSeleccionadoId());
            model.addAttribute("historialSeleccionado", historial);
        }
    }
}
