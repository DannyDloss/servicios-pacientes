package com.clinica.servicios.domain;

import com.clinica.servicios.domain.enums.Sexo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Representa a un paciente registrado en la clinica.
 * Sus datos se guardan en la tabla "pacientes".
 */
@Entity
@Table(name = "pacientes")
public class Paciente {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no debe superar 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotNull(message = "El sexo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sexo sexo;

    @Size(max = 20, message = "El telefono no debe superar 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    @Column(length = 120)
    private String email;

    @Size(max = 200, message = "La direccion no debe superar 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Size(max = 10, message = "El tipo de sangre no debe superar 10 caracteres")
    @Column(name = "tipo_sangre", length = 10)
    private String tipoSangre;

    @Size(max = 500, message = "Las alergias no deben superar 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String alergias;

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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
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
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Transient
    public String getFechaNacimientoTexto() {
        return fechaNacimiento == null ? "" : fechaNacimiento.format(FORMATO_FECHA);
    }

    @Transient
    public String getSexoTexto() {
        return sexo == null ? "" : sexo.getEtiqueta();
    }
}
