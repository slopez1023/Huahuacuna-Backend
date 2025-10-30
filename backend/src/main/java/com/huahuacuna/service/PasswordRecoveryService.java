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
 * Servicio para gestionar la recuperación de contraseñas.
 * <p>
 * Maneja la generación de tokens de recuperación, su validación
 * y el restablecimiento de contraseñas.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Duración en horas de validez del token de recuperación.
     */
    private static final int TOKEN_VALIDITY_HOURS = 24;

    /**
     * Solicita la recuperación de contraseña para un usuario.
     * Genera un token único y lo asocia al usuario.
     *
     * @param email el email del usuario que solicita recuperar su contraseña
     * @return el token generado
     * @throws RuntimeException si el usuario no existe
     */
    @Transactional
    public String requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Generar token único
        String token = UUID.randomUUID().toString();

        // Establecer el token y su fecha de expiración
        user.setResetPasswordToken(token);
        user.setResetPasswordExpires(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));

        userRepository.save(user);

        log.info("Token de recuperación generado para usuario: {}", email);

        return token;
    }

    /**
     * Restablece la contraseña de un usuario usando un token válido.
     *
     * @param token       el token de recuperación
     * @param newPassword la nueva contraseña (sin encriptar)
     * @throws RuntimeException si el token es inválido o ha expirado
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        // Verificar que el token no haya expirado
        if (!user.isResetTokenValid()) {
            throw new RuntimeException("El token ha expirado");
        }

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));

        // Limpiar el token de recuperación
        user.clearResetToken();

        userRepository.save(user);

        log.info("Contraseña restablecida exitosamente para usuario: {}", user.getEmail());
    }

    /**
     * Verifica si un token de recuperación es válido.
     *
     * @param token el token a verificar
     * @return true si el token existe y no ha expirado
     */
    public boolean isTokenValid(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(User::isResetTokenValid)
                .orElse(false);
    }

    /**
     * Obtiene el email asociado a un token de recuperación.
     *
     * @param token el token de recuperación
     * @return el email del usuario asociado al token
     * @throws RuntimeException si el token no existe
     */
    public String getEmailByToken(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
    }
}