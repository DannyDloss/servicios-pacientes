package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findAllByOrderByApellidosAscNombresAsc();

    List<Medico> findByActivoTrueOrderByApellidosAscNombresAsc();

    boolean existsByColegiatura(String colegiatura);

    boolean existsByColegiaturaAndIdNot(String colegiatura, Long id);

    @Query("""
            select m from Medico m
            where lower(m.nombres) like lower(concat('%', :q, '%'))
               or lower(m.apellidos) like lower(concat('%', :q, '%'))
               or lower(m.especialidad) like lower(concat('%', :q, '%'))
               or lower(m.colegiatura) like lower(concat('%', :q, '%'))
            order by m.apellidos asc, m.nombres asc
            """)
    List<Medico> buscar(@Param("q") String q);
}
