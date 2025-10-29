package com.huahuacuna.controller;

import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;
import com.huahuacuna.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de autenticación y registro.
 * Rutas disponibles:
 * - POST /api/auth/login     → Autentica un usuario
 * - GET  /api/auth/health    → Verifica disponibilidad del servicio
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // ============================================================
    // LOGIN EXISTENTE
    // ============================================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Recibida solicitud de login para: {}", loginRequest.getEmail());

        try {
            LoginResponse response = authService.authenticate(loginRequest);
            logger.info("Login exitoso para: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception ex) {
            logger.error("Error durante autenticación: {}", ex.getMessage());
            throw ex;
        }
    }

    // ============================================================
    // HEALTH CHECK
    // ============================================================
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok()
                .body(java.util.Map.of(
                        "status", "UP",
                        "message", "Servicio de autenticación disponible"
                ));
    }
}
