package com.clinica.servicios.service;

import com.clinica.servicios.domain.Medico;
import com.clinica.servicios.repository.CitaRepository;
import com.clinica.servicios.repository.HistorialMedicoRepository;
import com.clinica.servicios.repository.MedicoRepository;
import com.clinica.servicios.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final HistorialMedicoRepository historialRepository;
    private final RecetaRepository recetaRepository;

    public MedicoService(MedicoRepository medicoRepository,
                         CitaRepository citaRepository,
                         HistorialMedicoRepository historialRepository,
                         RecetaRepository recetaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.recetaRepository = recetaRepository;
    }

    @Transactional(readOnly = true)
    public List<Medico> listar(String q) {
        if (StringUtils.hasText(q)) {
            return medicoRepository.buscar(q.trim());
        }
        return medicoRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    @Transactional(readOnly = true)
    public List<Medico> listarActivos() {
        return medicoRepository.findByActivoTrueOrderByApellidosAscNombresAsc();
    }

    @Transactional(readOnly = true)
    public Medico obtener(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con id " + id));
    }

    @Transactional
    public Medico guardar(Medico datos) {
        String colegiatura = datos.getColegiatura() == null ? "" : datos.getColegiatura().trim();
        datos.setColegiatura(colegiatura);

        if (datos.getId() == null) {
            if (medicoRepository.existsByColegiatura(colegiatura)) {
                throw new BusinessException("Ya existe un medico con la colegiatura " + colegiatura);
            }
            return medicoRepository.save(datos);
        }

        if (medicoRepository.existsByColegiaturaAndIdNot(colegiatura, datos.getId())) {
            throw new BusinessException("Ya existe otro medico con la colegiatura " + colegiatura);
        }

        Medico medico = obtener(datos.getId());
        medico.setNombres(datos.getNombres());
        medico.setApellidos(datos.getApellidos());
        medico.setEspecialidad(datos.getEspecialidad());
        medico.setColegiatura(colegiatura);
        medico.setTelefono(datos.getTelefono());
        medico.setEmail(datos.getEmail());
        medico.setHorario(datos.getHorario());
        medico.setActivo(datos.isActivo());
        return medicoRepository.save(medico);
    }

    @Transactional
    public void eliminar(Long id) {
        Medico medico = obtener(id);
        if (citaRepository.existsByMedicoId(id)) {
            throw new BusinessException("No se puede eliminar el medico porque tiene citas registradas.");
        }
        if (historialRepository.existsByMedicoId(id)) {
            throw new BusinessException("No se puede eliminar el medico porque tiene historial clinico.");
        }
        if (recetaRepository.existsByMedicoId(id)) {
            throw new BusinessException("No se puede eliminar el medico porque tiene recetas emitidas.");
        }
        medicoRepository.delete(medico);
    }

    @Transactional(readOnly = true)
    public long contar() {
        return medicoRepository.count();
    }
}
