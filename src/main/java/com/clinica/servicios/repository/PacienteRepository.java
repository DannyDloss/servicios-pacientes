package com.clinica.servicios.repository;

import com.clinica.servicios.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de pacientes.
 * Spring Data JPA crea automaticamente las consultas basicas.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findAllByOrderByApellidosAscNombresAsc();

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);
}
