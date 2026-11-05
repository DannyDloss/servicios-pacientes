package com.clinica.servicios.repository;

import com.clinica.servicios.domain.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio del historial clinico.
 */
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    List<HistorialMedico> findAllByOrderByFechaConsultaDesc();

    List<HistorialMedico> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);

    Optional<HistorialMedico> findByCitaId(Long citaId);

    boolean existsByCitaId(Long citaId);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);
}
