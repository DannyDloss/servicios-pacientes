package com.clinica.servicios.domain.enums;

/**
 * Estados posibles de una cita: programada, atendida o cancelada.
 */
public enum EstadoCita {
    PROGRAMADA("Programada", "badge-programada"),
    ATENDIDA("Atendida", "badge-atendida"),
    CANCELADA("Cancelada", "badge-cancelada");

    private final String etiqueta;
    private final String claseCss;

    EstadoCita(String etiqueta, String claseCss) {
        this.etiqueta = etiqueta;
        this.claseCss = claseCss;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getClaseCss() {
        return claseCss;
    }
}
