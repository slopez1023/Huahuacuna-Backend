package com.huahuacuna.controller;

import com.huahuacuna.model.ForgotPasswordRequest;
import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;
import com.huahuacuna.model.ResetPasswordRequest;
import com.huahuacuna.service.AuthService;
import com.huahuacuna.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar la autenticación de usuarios.
 * <p>
 * Proporciona endpoints para login y recuperación de contraseña.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final PasswordRecoveryService passwordRecoveryService;

    /**
     * Endpoint para iniciar sesión.
     *
     * @param loginRequest credenciales del usuario
     * @return token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            log.info("Usuario autenticado: {}", loginRequest.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login exitoso",
                    "data", response
            ));
        } catch (RuntimeException e) {
            log.error("Error en login: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint para solicitar recuperación de contraseña.
     * Genera un token y lo asocia al usuario.
     *
     * @param request contiene el email del usuario
     * @return mensaje de confirmación y token (en desarrollo, en producción se enviaría por email)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String token = passwordRecoveryService.requestPasswordReset(request.getEmail());
            log.info("Solicitud de recuperación de contraseña para: {}", request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Se ha generado un token de recuperación");

            // En producción, el token se enviaría por email
            // Por ahora lo devolvemos en la respuesta para pruebas
            response.put("token", token);
            response.put("note", "En producción este token se enviaría por email");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al solicitar recuperación: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "No se pudo procesar la solicitud");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint para restablecer la contraseña usando un token.
     *
     * @param request contiene el token y la nueva contraseña
     * @return mensaje de confirmación
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordRecoveryService.resetPassword(request.getToken(), request.getNewPassword());
            log.info("Contraseña restablecida exitosamente");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contraseña actualizada exitosamente"
            ));
        } catch (RuntimeException e) {
            log.error("Error al restablecer contraseña: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint para verificar si un token de recuperación es válido.
     *
     * @param token el token a verificar
     * @return información sobre la validez del token
     */
    @GetMapping("/verify-token/{token}")
    public ResponseEntity<?> verifyToken(@PathVariable String token) {
        try {
            boolean isValid = passwordRecoveryService.isTokenValid(token);

            if (isValid) {
                String email = passwordRecoveryService.getEmailByToken(token);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "valid", true,
                        "email", email
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "valid", false,
                        "message", "Token inválido o expirado"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "valid", false,
                    "message", "Token no encontrado"
            ));
        }
    }
}