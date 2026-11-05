package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findAllByOrderByApellidosAscNombresAsc();

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    @Query("""
            select p from Paciente p
            where lower(p.nombres) like lower(concat('%', :q, '%'))
               or lower(p.apellidos) like lower(concat('%', :q, '%'))
               or lower(p.documento) like lower(concat('%', :q, '%'))
            order by p.apellidos asc, p.nombres asc
            """)
    List<Paciente> buscar(@Param("q") String q);
}
