package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de medicos.
 */
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findAllByOrderByApellidosAscNombresAsc();

    List<Medico> findByActivoTrueOrderByApellidosAscNombresAsc();

    boolean existsByColegiatura(String colegiatura);

    boolean existsByColegiaturaAndIdNot(String colegiatura, Long id);
}
