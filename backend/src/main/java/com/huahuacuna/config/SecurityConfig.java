package com.huahuacuna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad de la aplicación.
 * <p>
 * Define las reglas de autorización, encriptación de contraseñas
 * y configuración de acceso a endpoints según roles.
 * </p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Bean para encriptar contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración del filtro de seguridad HTTP.
     * Define qué endpoints requieren autenticación y qué roles pueden acceder.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF para facilitar pruebas con Postman
                .csrf(csrf -> csrf.disable())

                // Configura las reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - no requieren autenticación
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/verify-token/**",
                                "/api/auth/reset-password",
                                "/h2-console/**"
                        ).permitAll()

                        // ⭐ ENDPOINTS DE DONACIONES - NUEVOS ⭐
                        .requestMatchers(
                                "/api/donations",              // POST - Crear donación (público)
                                "/api/donations/export"        // GET - Exportar CSV (público)
                        ).permitAll()

                        // Endpoints de donaciones solo para ADMIN
                        .requestMatchers(
                                "/api/donations/reports",      // GET - Ver reportes
                                "/api/donations/{id}",         // GET - Ver donación específica
                                "/api/donations/{id}/status"   // PATCH - Actualizar estado
                        ).hasRole("ADMIN")

                        // Endpoints solo para ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints para VOLUNTARIOS
                        .requestMatchers("/api/voluntario/**").hasAnyRole("ADMIN", "VOLUNTARIO")

                        // Endpoints para APADRINADOS
                        .requestMatchers("/api/apadrinado/**").hasAnyRole("ADMIN", "APADRINADO")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // Desactiva login básico y logout predeterminados
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Permite que se muestre la consola H2 en frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}