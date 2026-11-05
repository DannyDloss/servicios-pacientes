package com.clinica.servicios.service;

import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.domain.Receta;
import com.clinica.servicios.domain.RecetaItem;
import com.clinica.servicios.domain.enums.EstadoReceta;
import com.clinica.servicios.repository.HistorialMedicoRepository;
import com.clinica.servicios.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final HistorialMedicoRepository historialRepository;

    public RecetaService(RecetaRepository recetaRepository,
                         HistorialMedicoRepository historialRepository) {
        this.recetaRepository = recetaRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public List<Receta> listar(String q) {
        if (StringUtils.hasText(q)) {
            return recetaRepository.buscar(q.trim());
        }
        return recetaRepository.findAllByOrderByFechaEmisionDesc();
    }

    @Transactional(readOnly = true)
    public List<Receta> listarPorPaciente(Long pacienteId) {
        return recetaRepository.findByPacienteIdOrderByFechaEmisionDesc(pacienteId);
    }

    @Transactional(readOnly = true)
    public Receta obtener(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id " + id));
    }

    @Transactional
    public Receta crear(Receta datos) {
        if (datos.getHistorialSeleccionadoId() == null) {
            throw new BusinessException("Debe seleccionar un historial clinico.");
        }

        HistorialMedico historial = historialRepository.findById(datos.getHistorialSeleccionadoId())
                .orElseThrow(() -> new BusinessException("El historial seleccionado no existe."));

        List<RecetaItem> items = datos.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("Debe agregar al menos un medicamento.");
        }

        long validos = items.stream()
                .filter(i -> i != null && StringUtils.hasText(i.getMedicamento()))
                .count();
        if (validos == 0) {
            throw new BusinessException("Debe agregar al menos un medicamento.");
        }

        Receta receta = new Receta();
        receta.setHistorial(historial);
        receta.setPaciente(historial.getPaciente());
        receta.setMedico(historial.getMedico());
        receta.setFechaEmision(datos.getFechaEmision() == null ? LocalDateTime.now() : datos.getFechaEmision());
        receta.setIndicacionesGenerales(datos.getIndicacionesGenerales());
        receta.setEstado(EstadoReceta.EMITIDA);

        for (RecetaItem item : items) {
            if (item == null || !StringUtils.hasText(item.getMedicamento())) {
                continue;
            }
            if (!StringUtils.hasText(item.getDosis()) || !StringUtils.hasText(item.getFrecuencia())) {
                throw new BusinessException("Complete la dosis y la frecuencia de todos los medicamentos.");
            }
            RecetaItem nuevo = new RecetaItem();
            nuevo.setMedicamento(item.getMedicamento().trim());
            nuevo.setPresentacion(item.getPresentacion());
            nuevo.setDosis(item.getDosis().trim());
            nuevo.setFrecuencia(item.getFrecuencia().trim());
            nuevo.setDuracion(item.getDuracion());
            nuevo.setCantidad(item.getCantidad());
            nuevo.setIndicaciones(item.getIndicaciones());
            receta.addItem(nuevo);
        }

        return recetaRepository.save(receta);
    }

    @Transactional
    public Receta anular(Long id) {
        Receta receta = obtener(id);
        receta.setEstado(EstadoReceta.ANULADA);
        return recetaRepository.save(receta);
    }

    @Transactional(readOnly = true)
    public long contarEmitidas() {
        return recetaRepository.countByEstado(EstadoReceta.EMITIDA);
    }
}
