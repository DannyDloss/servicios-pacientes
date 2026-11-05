package com.clinica.servicios.web.controller;

import com.clinica.servicios.service.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(ResourceNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recursoNoEncontrado(NoResourceFoundException ex, Model model) {
        model.addAttribute("mensaje", "La pagina solicitada no existe.");
        return "error/404";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String metodoNoSoportado(HttpRequestMethodNotSupportedException ex, Model model) {
        model.addAttribute("mensaje", "La accion solicitada no esta disponible para esta ruta.");
        return "error/405";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String errorGeneral(Exception ex, Model model) {
        log.error("Error no controlado en la aplicacion", ex);
        model.addAttribute("mensaje", "Ocurrio un error inesperado. Revise los registros del servidor.");
        return "error/500";
    }
}
