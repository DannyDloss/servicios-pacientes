package com.clinica.servicios.service;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.HistorialMedico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final CitaService citaService;
    private final HistorialService historialService;
    private final RecetaService recetaService;

    public DashboardService(PacienteService pacienteService,
                            MedicoService medicoService,
                            CitaService citaService,
                            HistorialService historialService,
                            RecetaService recetaService) {
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.citaService = citaService;
        this.historialService = historialService;
        this.recetaService = recetaService;
    }

    @Transactional(readOnly = true)
    public long totalPacientes() {
        return pacienteService.contar();
    }

    @Transactional(readOnly = true)
    public long totalMedicos() {
        return medicoService.contar();
    }

    @Transactional(readOnly = true)
    public long citasHoy() {
        return citaService.contarHoy();
    }

    @Transactional(readOnly = true)
    public long citasProgramadas() {
        return citaService.contarProgramadas();
    }

    @Transactional(readOnly = true)
    public long recetasEmitidas() {
        return recetaService.contarEmitidas();
    }

    @Transactional(readOnly = true)
    public List<Cita> proximasCitas() {
        return citaService.proximas();
    }

    @Transactional(readOnly = true)
    public List<HistorialMedico> historialesRecientes() {
        return historialService.recientes();
    }
}
