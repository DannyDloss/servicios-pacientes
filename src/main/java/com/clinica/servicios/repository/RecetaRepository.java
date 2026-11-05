package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de recetas.
 */
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findAllByOrderByFechaEmisionDesc();

    List<Receta> findByPacienteIdOrderByFechaEmisionDesc(Long pacienteId);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);
}
