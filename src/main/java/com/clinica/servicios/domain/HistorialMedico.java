package com.clinica.servicios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Guarda la atencion medica de una cita.
 * Incluye el diagnostico, el tratamiento y los signos vitales del paciente.
 */
@Entity
@Table(name = "historiales")
public class HistorialMedico {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @OneToOne
    @JoinColumn(name = "cita_id", unique = true)
    private Cita cita;

    @NotNull(message = "La fecha de consulta es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 255, message = "El motivo no debe superar 255 caracteres")
    @Column(name = "motivo_consulta", nullable = false, length = 255)
    private String motivoConsulta;

    @NotBlank(message = "El diagnostico es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(precision = 5, scale = 2)
    private BigDecimal talla;

    @Size(max = 20, message = "La presion arterial no debe superar 20 caracteres")
    @Column(name = "presion_arterial", length = 20)
    private String presionArterial;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private Long pacienteSeleccionadoId;

    @Transient
    private Long medicoSeleccionadoId;

    @Transient
    private Long citaSeleccionadaId;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (fechaConsulta == null) {
            fechaConsulta = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getTalla() {
        return talla;
    }

    public void setTalla(BigDecimal talla) {
        this.talla = talla;
    }

    public String getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPacienteSeleccionadoId() {
        return pacienteSeleccionadoId;
    }

    public void setPacienteSeleccionadoId(Long pacienteSeleccionadoId) {
        this.pacienteSeleccionadoId = pacienteSeleccionadoId;
    }

    public Long getMedicoSeleccionadoId() {
        return medicoSeleccionadoId;
    }

    public void setMedicoSeleccionadoId(Long medicoSeleccionadoId) {
        this.medicoSeleccionadoId = medicoSeleccionadoId;
    }

    public Long getCitaSeleccionadaId() {
        return citaSeleccionadaId;
    }

    public void setCitaSeleccionadaId(Long citaSeleccionadaId) {
        this.citaSeleccionadaId = citaSeleccionadaId;
    }

    @Transient
    public String getFechaConsultaTexto() {
        return fechaConsulta == null ? "" : fechaConsulta.format(FORMATO_FECHA_HORA);
    }

    @Transient
    public String getDiagnosticoResumen() {
        if (diagnostico == null) {
            return "";
        }
        return diagnostico.length() > 90 ? diagnostico.substring(0, 90) + "..." : diagnostico;
    }
}
