package com.huahuacuna.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS (Cross-Origin Resource Sharing).
 *
 * Permite que el frontend (React/Next.js) en localhost:3000
 * realice solicitudes al backend en localhost:8080.
 *
 * En producción, reemplazar los valores con dominios reales
 * y más restrictivos por razones de seguridad.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Permitir origen del frontend en desarrollo
                .allowedOrigins(
                        "http://localhost:3000",     // Next.js dev server
                        "http://localhost:3001",     // Puerto alternativo
                        "http://127.0.0.1:3000",     // Acceso local
                        "http://127.0.0.1:3001"      // Acceso local alternativo
                )
                // Métodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // Headers permitidos en la solicitud
                .allowedHeaders("*")
                // Headers que se exponen en la respuesta
                .exposedHeaders("Authorization", "Content-Type")
                // Permitir envío de cookies/credenciales
                .allowCredentials(true)
                // Tiempo de vida del preflight (en segundos)
                .maxAge(3600);

        // Configuración adicional para otros endpoints si es necesario
        registry.addMapping("/api/admin/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .maxAge(3600);
    }
}