package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de citas.
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaHoraDesc();

    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    // Revisa si el medico ya tiene otra cita en la misma fecha y hora.
    @Query("""
            select count(c) > 0 from Cita c
            where c.medico.id = :medicoId
              and c.fechaHora = :fechaHora
              and c.estado <> :cancelada
              and (:id is null or c.id <> :id)
            """)
    boolean existeConflictoMedico(@Param("medicoId") Long medicoId,
                                  @Param("fechaHora") LocalDateTime fechaHora,
                                  @Param("cancelada") EstadoCita cancelada,
                                  @Param("id") Long id);

    // Revisa si el paciente ya tiene otra cita en la misma fecha y hora.
    @Query("""
            select count(c) > 0 from Cita c
            where c.paciente.id = :pacienteId
              and c.fechaHora = :fechaHora
              and c.estado <> :cancelada
              and (:id is null or c.id <> :id)
            """)
    boolean existeConflictoPaciente(@Param("pacienteId") Long pacienteId,
                                    @Param("fechaHora") LocalDateTime fechaHora,
                                    @Param("cancelada") EstadoCita cancelada,
                                    @Param("id") Long id);
}
