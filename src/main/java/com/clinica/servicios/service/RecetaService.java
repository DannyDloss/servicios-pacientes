package com.clinica.servicios.service;

import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.domain.Receta;
import com.clinica.servicios.domain.RecetaItem;
import com.clinica.servicios.domain.enums.EstadoReceta;
import com.clinica.servicios.repository.HistorialMedicoRepository;
import com.clinica.servicios.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio para las recetas.
 */
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
    public List<Receta> listar() {
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

        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new BusinessException("Debe agregar al menos un medicamento.");
        }

        Receta receta = new Receta();
        receta.setHistorial(historial);
        receta.setPaciente(historial.getPaciente());
        receta.setMedico(historial.getMedico());
        receta.setFechaEmision(datos.getFechaEmision() == null ? LocalDateTime.now() : datos.getFechaEmision());
        receta.setIndicacionesGenerales(datos.getIndicacionesGenerales());
        receta.setEstado(EstadoReceta.EMITIDA);

        int medicamentosValidos = 0;
        for (RecetaItem item : datos.getItems()) {
            if (item == null || !textoValido(item.getMedicamento())) {
                continue;
            }
            if (!textoValido(item.getDosis()) || !textoValido(item.getFrecuencia())) {
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
            medicamentosValidos++;
        }

        if (medicamentosValidos == 0) {
            throw new BusinessException("Debe agregar al menos un medicamento.");
        }

        return recetaRepository.save(receta);
    }

    @Transactional
    public Receta anular(Long id) {
        Receta receta = obtener(id);
        if (receta.getEstado() == EstadoReceta.ANULADA) {
            throw new BusinessException("La receta ya esta anulada.");
        }
        receta.setEstado(EstadoReceta.ANULADA);
        return recetaRepository.save(receta);
    }

    private boolean textoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}
