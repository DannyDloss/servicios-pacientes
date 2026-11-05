package com.clinica.servicios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Representa a un medico que atiende en la clinica.
 * Sus datos se guardan en la tabla "medicos".
 */
@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres no deben superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos no deben superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String apellidos;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 80, message = "La especialidad no debe superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String especialidad;

    @NotBlank(message = "La colegiatura es obligatoria")
    @Size(max = 30, message = "La colegiatura no debe superar 30 caracteres")
    @Column(nullable = false, unique = true, length = 30)
    private String colegiatura;

    @Size(max = 20, message = "El telefono no debe superar 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    @Column(length = 120)
    private String email;

    @Size(max = 120, message = "El horario no debe superar 120 caracteres")
    @Column(length = 120)
    private String horario;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getColegiatura() {
        return colegiatura;
    }

    public void setColegiatura(String colegiatura) {
        this.colegiatura = colegiatura;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Transient
    public String getNombreCompleto() {
        String n = nombres == null ? "" : nombres.trim();
        String a = apellidos == null ? "" : apellidos.trim();
        return (n + " " + a).trim();
    }

    @Transient
    public String getNombreConEspecialidad() {
        return getNombreCompleto() + " - " + (especialidad == null ? "" : especialidad);
    }
}
