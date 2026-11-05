package com.clinica.servicios.domain.enums;

/**
 * Opciones de sexo que se pueden registrar para un paciente.
 */
public enum Sexo {
    MASCULINO("Masculino"),
    FEMENINO("Femenino"),
    OTRO("Otro");

    private final String etiqueta;

    Sexo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
