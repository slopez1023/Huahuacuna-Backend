package com.huahuacuna.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Esta configuración habilita el envío asíncrono de emails
    // para que no bloqueen la respuesta HTTP
}