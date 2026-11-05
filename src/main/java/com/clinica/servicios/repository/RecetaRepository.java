package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Receta;
import com.clinica.servicios.domain.enums.EstadoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findAllByOrderByFechaEmisionDesc();

    List<Receta> findByPacienteIdOrderByFechaEmisionDesc(Long pacienteId);

    List<Receta> findByHistorialId(Long historialId);

    long countByEstado(EstadoReceta estado);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    boolean existsByHistorialId(Long historialId);

    @Query("""
            select distinct r from Receta r
            join r.paciente p
            join r.medico m
            where lower(p.nombres) like lower(concat('%', :q, '%'))
               or lower(p.apellidos) like lower(concat('%', :q, '%'))
               or lower(p.documento) like lower(concat('%', :q, '%'))
               or lower(m.nombres) like lower(concat('%', :q, '%'))
               or lower(m.apellidos) like lower(concat('%', :q, '%'))
            order by r.fechaEmision desc
            """)
    List<Receta> buscar(@Param("q") String q);
}
