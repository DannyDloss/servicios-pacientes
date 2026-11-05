package com.clinica.servicios.service;

/**
 * Se usa para mostrar errores de negocio al usuario.
 * Por ejemplo: documento duplicado o cita en una fecha ocupada.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
