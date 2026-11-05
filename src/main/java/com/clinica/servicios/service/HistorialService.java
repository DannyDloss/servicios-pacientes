package com.clinica.servicios.service;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.HistorialMedico;
import com.clinica.servicios.domain.Medico;
import com.clinica.servicios.domain.Paciente;
import com.clinica.servicios.domain.enums.EstadoCita;
import com.clinica.servicios.repository.CitaRepository;
import com.clinica.servicios.repository.HistorialMedicoRepository;
import com.clinica.servicios.repository.MedicoRepository;
import com.clinica.servicios.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de negocio para el historial clinico.
 */
@Service
public class HistorialService {

    private final HistorialMedicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;

    public HistorialService(HistorialMedicoRepository historialRepository,
                            PacienteRepository pacienteRepository,
                            MedicoRepository medicoRepository,
                            CitaRepository citaRepository) {
        this.historialRepository = historialRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    @Transactional(readOnly = true)
    public List<HistorialMedico> listar() {
        return historialRepository.findAllByOrderByFechaConsultaDesc();
    }

    @Transactional(readOnly = true)
    public List<HistorialMedico> listarPorPaciente(Long pacienteId) {
        return historialRepository.findByPacienteIdOrderByFechaConsultaDesc(pacienteId);
    }

    @Transactional(readOnly = true)
    public HistorialMedico obtener(Long id) {
        return historialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Optional<HistorialMedico> buscarPorCita(Long citaId) {
        return historialRepository.findByCitaId(citaId);
    }

    @Transactional
    public HistorialMedico guardar(HistorialMedico datos) {
        boolean nuevo = datos.getId() == null;
        HistorialMedico historial = nuevo ? new HistorialMedico() : obtener(datos.getId());

        if (nuevo) {
            if (datos.getCitaSeleccionadaId() != null) {
                Cita cita = citaRepository.findById(datos.getCitaSeleccionadaId())
                        .orElseThrow(() -> new BusinessException("La cita seleccionada no existe."));
                if (cita.getEstado() == EstadoCita.CANCELADA) {
                    throw new BusinessException("No se puede registrar atencion de una cita cancelada.");
                }
                if (historialRepository.existsByCitaId(cita.getId())) {
                    throw new BusinessException("La cita ya tiene un historial registrado.");
                }
                historial.setCita(cita);
                historial.setPaciente(cita.getPaciente());
                historial.setMedico(cita.getMedico());
            } else {
                Paciente paciente = pacienteRepository.findById(datos.getPacienteSeleccionadoId())
                        .orElseThrow(() -> new BusinessException("Debe seleccionar un paciente valido."));
                Medico medico = medicoRepository.findById(datos.getMedicoSeleccionadoId())
                        .orElseThrow(() -> new BusinessException("Debe seleccionar un medico valido."));
                historial.setPaciente(paciente);
                historial.setMedico(medico);
            }
        }

        if (datos.getFechaConsulta() == null) {
            historial.setFechaConsulta(LocalDateTime.now());
        } else {
            historial.setFechaConsulta(datos.getFechaConsulta());
        }
        historial.setMotivoConsulta(datos.getMotivoConsulta());
        historial.setDiagnostico(datos.getDiagnostico());
        historial.setTratamiento(datos.getTratamiento());
        historial.setNotas(datos.getNotas());
        historial.setPeso(datos.getPeso());
        historial.setTalla(datos.getTalla());
        historial.setPresionArterial(datos.getPresionArterial());
        historial.setTemperatura(datos.getTemperatura());
        historial.setFrecuenciaCardiaca(datos.getFrecuenciaCardiaca());

        HistorialMedico guardado = historialRepository.save(historial);

        if (guardado.getCita() != null && guardado.getCita().getEstado() != EstadoCita.ATENDIDA) {
            Cita cita = guardado.getCita();
            cita.setEstado(EstadoCita.ATENDIDA);
            citaRepository.save(cita);
        }

        return guardado;
    }
}
