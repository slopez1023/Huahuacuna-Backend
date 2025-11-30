package com.huahuacuna.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Configuración CORS explícita
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    // ✅ CAMBIO CRÍTICO: Usar setAllowedOriginPatterns en lugar de setAllowedOrigins
                    config.setAllowedOriginPatterns(List.of(
                            "http://localhost:3000",
                            "http://127.0.0.1:3000",
                            "http://localhost:3001",
                            "http://127.0.0.1:3001"
                    ));

                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // ✅ CRÍTICO: Permitir OPTIONS sin autenticación (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints públicos de autenticación
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/verify-token/**",
                                "/api/auth/reset-password"
                        ).permitAll()

                        // ⭐ ENDPOINTS DE DONACIONES - PÚBLICOS ⭐
                        .requestMatchers(HttpMethod.POST, "/api/donations").permitAll()     // ✅ Crear donación
                        .requestMatchers(HttpMethod.GET, "/api/donations/export").permitAll() // ✅ Exportar

                        // Endpoints de donaciones solo para ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/donations/reports").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/donations/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/donations/{id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/donations").hasRole("ADMIN")

                        // Formularios públicos
                        .requestMatchers(
                                "/api/applications/volunteer",
                                "/api/applications/sponsor"
                        ).permitAll()

                        // Consola H2 (solo desarrollo)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Endpoints protegidos por rol ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/applications", "/api/applications/**").hasRole("ADMIN")
                        .requestMatchers("/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints por roles específicos
                        .requestMatchers("/api/voluntario/**").hasAnyRole("ADMIN", "VOLUNTARIO")
                        .requestMatchers("/api/padrinos/**").hasAnyRole("ADMIN", "PADRINO")

                        // ========== CHAT DEL ADMINISTRADOR ==========
                        .requestMatchers("/api/admin/chat/**").hasRole("ADMIN")

                        // ⭐ NUEVOS MÓDULOS INTEGRADOS ⭐

                        // 1. NIÑOS (Privacidad protegida)
                        // Ver lista y detalles: Solo ADMIN y PADRINOS (Usamos "APADRINADO" para coincidir con tu BD)
                        .requestMatchers(HttpMethod.GET, "/api/children/**").hasAnyRole("ADMIN", "APADRINADO")
                        // Crear, Editar, Eliminar: Solo ADMIN
                        .requestMatchers("/api/children/**").hasRole("ADMIN")

                        // 2. EVENTOS Y PROYECTOS (Públicos para ver)
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                        // Gestión de Eventos/Proyectos: Solo ADMIN
                        .requestMatchers("/api/events/**").hasRole("ADMIN")
                        .requestMatchers("/api/projects/**").hasRole("ADMIN")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // Agregar filtro JWT antes del filtro de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Permitir H2 console en frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}