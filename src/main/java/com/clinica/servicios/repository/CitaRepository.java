package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Cita;
import com.clinica.servicios.domain.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaHoraDesc();

    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    long countByEstado(EstadoCita estado);

    long countByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Cita> findTop5ByFechaHoraAfterAndEstadoOrderByFechaHoraAsc(LocalDateTime desde, EstadoCita estado);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    @Query("""
            select c from Cita c
            where (:estado is null or c.estado = :estado)
              and (:medicoId is null or c.medico.id = :medicoId)
              and (:desde is null or c.fechaHora >= :desde)
              and (:hasta is null or c.fechaHora < :hasta)
            order by c.fechaHora asc
            """)
    List<Cita> buscar(@Param("estado") EstadoCita estado,
                      @Param("medicoId") Long medicoId,
                      @Param("desde") LocalDateTime desde,
                      @Param("hasta") LocalDateTime hasta);

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
