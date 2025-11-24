package com.huahuacuna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Clase principal de la aplicación Spring Boot.
 * Inicializa el servidor y carga todas las configuraciones.
 */
@SpringBootApplication
@EnableAsync
public class HuahuacunaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuahuacunaApplication.class, args);
    }
}