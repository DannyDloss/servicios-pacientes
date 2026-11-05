package com.clinica.servicios.domain.enums;

public enum EstadoReceta {
    EMITIDA("Emitida", "badge-emitida"),
    ANULADA("Anulada", "badge-anulada");

    private final String etiqueta;
    private final String claseCss;

    EstadoReceta(String etiqueta, String claseCss) {
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
