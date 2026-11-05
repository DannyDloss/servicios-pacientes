package com.clinica.servicios.repository;

import com.clinica.servicios.domain.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    List<HistorialMedico> findAllByOrderByFechaConsultaDesc();

    List<HistorialMedico> findTop5ByOrderByFechaConsultaDesc();

    List<HistorialMedico> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);

    Optional<HistorialMedico> findByCitaId(Long citaId);

    boolean existsByCitaId(Long citaId);

    boolean existsByCitaIdAndIdNot(Long citaId, Long id);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    @Query("""
            select h from HistorialMedico h
            join h.paciente p
            join h.medico m
            where lower(p.nombres) like lower(concat('%', :q, '%'))
               or lower(p.apellidos) like lower(concat('%', :q, '%'))
               or lower(p.documento) like lower(concat('%', :q, '%'))
               or lower(h.diagnostico) like lower(concat('%', :q, '%'))
               or lower(m.nombres) like lower(concat('%', :q, '%'))
               or lower(m.apellidos) like lower(concat('%', :q, '%'))
            order by h.fechaConsulta desc
            """)
    List<HistorialMedico> buscar(@Param("q") String q);
}
