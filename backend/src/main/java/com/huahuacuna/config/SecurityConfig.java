package com.huahuacuna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
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
     * Permite el acceso libre a los endpoints de autenticación y la consola H2.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF para facilitar pruebas con Postman
                .csrf(csrf -> csrf.disable())

                // Configura qué rutas se pueden acceder sin autenticación
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll() // Permitir login/register/H2
                        .anyRequest().permitAll() // Permitir todo por ahora (puedes ajustar después)
                )

                // Desactiva login básico y logout predeterminados
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Permite que se muestre la consola H2
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
