package com.clinica.servicios.service;

import com.clinica.servicios.domain.Paciente;
import com.clinica.servicios.repository.CitaRepository;
import com.clinica.servicios.repository.HistorialMedicoRepository;
import com.clinica.servicios.repository.PacienteRepository;
import com.clinica.servicios.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio para los pacientes.
 */
@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final HistorialMedicoRepository historialRepository;
    private final RecetaRepository recetaRepository;

    public PacienteService(PacienteRepository pacienteRepository,
                           CitaRepository citaRepository,
                           HistorialMedicoRepository historialRepository,
                           RecetaRepository recetaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.recetaRepository = recetaRepository;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listar() {
        return pacienteRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id " + id));
    }

    @Transactional
    public Paciente guardar(Paciente datos) {
        String documento = datos.getDocumento() == null ? "" : datos.getDocumento().trim();
        datos.setDocumento(documento);

        if (datos.getId() == null) {
            if (pacienteRepository.existsByDocumento(documento)) {
                throw new BusinessException("Ya existe un paciente con el documento " + documento);
            }
            return pacienteRepository.save(datos);
        }

        if (pacienteRepository.existsByDocumentoAndIdNot(documento, datos.getId())) {
            throw new BusinessException("Ya existe otro paciente con el documento " + documento);
        }

        Paciente paciente = obtener(datos.getId());
        paciente.setNombres(datos.getNombres());
        paciente.setApellidos(datos.getApellidos());
        paciente.setDocumento(documento);
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setSexo(datos.getSexo());
        paciente.setTelefono(datos.getTelefono());
        paciente.setEmail(datos.getEmail());
        paciente.setDireccion(datos.getDireccion());
        paciente.setTipoSangre(datos.getTipoSangre());
        paciente.setAlergias(datos.getAlergias());
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public void eliminar(Long id) {
        Paciente paciente = obtener(id);
        if (citaRepository.existsByPacienteId(id)) {
            throw new BusinessException("No se puede eliminar el paciente porque tiene citas registradas.");
        }
        if (historialRepository.existsByPacienteId(id)) {
            throw new BusinessException("No se puede eliminar el paciente porque tiene historial clinico.");
        }
        if (recetaRepository.existsByPacienteId(id)) {
            throw new BusinessException("No se puede eliminar el paciente porque tiene recetas emitidas.");
        }
        pacienteRepository.delete(paciente);
    }
}
