package com.clinica.servicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion.
 * Al ejecutarla, Spring Boot inicia el servidor web y carga el sistema.
 */
@SpringBootApplication
public class ServiciosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiciosApplication.class, args);
    }
}
