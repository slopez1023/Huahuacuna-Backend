package com.huahuacuna.service;

import com.huahuacuna.model.User;
import com.huahuacuna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para gestionar la recuperación de contraseña.
 * Genera tokens, valida y permite resetear contraseñas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Token válido por 1 hora
    private static final int TOKEN_EXPIRATION_HOURS = 1;

    /**
     * Solicita un reset de contraseña generando un token y enviando email.
     *
     * @param email Email del usuario
     * @return Token generado (para desarrollo, en producción solo se envía por email)
     */
    @Transactional
    public String requestPasswordReset(String email) {
        log.info("Solicitud de reset de contraseña para: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        if (!user.getIsActive()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        // Generar token único
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);

        // Guardar token en la base de datos
        user.setResetPasswordToken(token);
        user.setResetPasswordExpires(expirationTime);
        userRepository.save(user);

        log.info("Token de reset generado para: {} (expira: {})", email, expirationTime);

        // Enviar email con el token
        try {
            emailService.sendPasswordResetEmail(email, user.getFullName(), token);
            log.info("Email de recuperación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación: {}", e.getMessage());
            // En producción, podrías querer revertir la transacción aquí
            // throw new RuntimeException("Error al enviar el email de recuperación", e);
        }

        // Retornar el token (solo para desarrollo/testing)
        // En producción, este método debería retornar void
        return token;
    }

    /**
     * Verifica si un token es válido y no ha expirado.
     *
     * @param token Token a verificar
     * @return true si es válido, false si no
     */
    public boolean isTokenValid(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(user -> {
                    if (user.getResetPasswordExpires() == null) {
                        return false;
                    }
                    boolean isValid = LocalDateTime.now().isBefore(user.getResetPasswordExpires());
                    log.debug("Token {} es válido: {}", token, isValid);
                    return isValid;
                })
                .orElse(false);
    }

    /**
     * Obtiene el email asociado a un token.
     *
     * @param token Token de reset
     * @return Email del usuario
     */
    public String getEmailByToken(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
    }

    /**
     * Resetea la contraseña usando un token válido.
     *
     * @param token Token de reset
     * @param newPassword Nueva contraseña
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Intentando resetear contraseña con token: {}", token);

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        // Verificar que el token no haya expirado
        if (user.getResetPasswordExpires() == null ||
                LocalDateTime.now().isAfter(user.getResetPasswordExpires())) {
            throw new RuntimeException("El token ha expirado");
        }

        // Actualizar contraseña y limpiar token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        userRepository.save(user);

        log.info("Contraseña reseteada exitosamente para: {}", user.getEmail());
    }

    /**
     * Limpia tokens expirados (puede ejecutarse como tarea programada)
     */
    @Transactional
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        userRepository.findAll().forEach(user -> {
            if (user.getResetPasswordExpires() != null &&
                    now.isAfter(user.getResetPasswordExpires())) {
                user.setResetPasswordToken(null);
                user.setResetPasswordExpires(null);
                userRepository.save(user);
                log.debug("Token expirado eliminado para usuario: {}", user.getEmail());
            }
        });
    }
}