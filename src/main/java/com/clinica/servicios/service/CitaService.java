package com.clinica.servicios.service;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.Medico;
import com.clinica.servicios.domain.Paciente;
import com.clinica.servicios.domain.enums.EstadoCita;
import com.clinica.servicios.repository.CitaRepository;
import com.clinica.servicios.repository.MedicoRepository;
import com.clinica.servicios.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaService(CitaRepository citaRepository,
                       PacienteRepository pacienteRepository,
                       MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Cita> listar(LocalDate fecha, EstadoCita estado, Long medicoId) {
        LocalDateTime desde = fecha == null ? null : fecha.atStartOfDay();
        LocalDateTime hasta = fecha == null ? null : fecha.plusDays(1).atStartOfDay();
        return citaRepository.buscar(estado, medicoId, desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    @Transactional(readOnly = true)
    public List<Cita> proximas() {
        return citaRepository.findTop5ByFechaHoraAfterAndEstadoOrderByFechaHoraAsc(
                LocalDateTime.now(), EstadoCita.PROGRAMADA);
    }

    @Transactional(readOnly = true)
    public Cita obtener(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id " + id));
    }

    @Transactional
    public Cita guardar(Cita datos) {
        if (datos.getFechaHora() == null) {
            throw new BusinessException("La fecha y hora de la cita son obligatorias.");
        }

        Paciente paciente = pacienteRepository.findById(datos.getPacienteSeleccionadoId())
                .orElseThrow(() -> new BusinessException("Debe seleccionar un paciente valido."));
        Medico medico = medicoRepository.findById(datos.getMedicoSeleccionadoId())
                .orElseThrow(() -> new BusinessException("Debe seleccionar un medico valido."));

        boolean nueva = datos.getId() == null;
        if (nueva && datos.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede agendar una cita en una fecha u hora pasada.");
        }

        if (citaRepository.existeConflictoMedico(medico.getId(), datos.getFechaHora(),
                EstadoCita.CANCELADA, datos.getId())) {
            throw new BusinessException("El medico ya tiene una cita en esa fecha y hora.");
        }

        if (citaRepository.existeConflictoPaciente(paciente.getId(), datos.getFechaHora(),
                EstadoCita.CANCELADA, datos.getId())) {
            throw new BusinessException("El paciente ya tiene una cita en esa fecha y hora.");
        }

        Cita cita = nueva ? new Cita() : obtener(datos.getId());
        if (nueva) {
            cita.setEstado(EstadoCita.PROGRAMADA);
        }
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaHora(datos.getFechaHora());
        cita.setMotivo(datos.getMotivo());
        cita.setObservaciones(datos.getObservaciones());
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = obtener(id);
        if (cita.getEstado() == EstadoCita.ATENDIDA && nuevoEstado != EstadoCita.ATENDIDA) {
            throw new BusinessException("Una cita atendida no puede cambiar de estado.");
        }
        if (cita.getEstado() == EstadoCita.CANCELADA && nuevoEstado == EstadoCita.ATENDIDA) {
            throw new BusinessException("Una cita cancelada no puede marcarse como atendida.");
        }
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    @Transactional(readOnly = true)
    public long contarHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        return citaRepository.countByFechaHoraBetween(inicio, inicio.plusDays(1));
    }

    @Transactional(readOnly = true)
    public long contarProgramadas() {
        return citaRepository.countByEstado(EstadoCita.PROGRAMADA);
    }
}
