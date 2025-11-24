package com.huahuacuna.service;

import com.huahuacuna.model.LoginRequest;
import com.huahuacuna.model.LoginResponse;
import com.huahuacuna.model.User;
import com.huahuacuna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // ✅ Inyección del JwtService

    /**
     * Autentica un usuario con email y contraseña.
     *
     * @param loginRequest credenciales del usuario
     * @return respuesta con token JWT y datos del usuario
     * @throws RuntimeException si las credenciales son inválidas
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Verificar que la cuenta esté activa
        if (!user.getIsActive()) {
            throw new RuntimeException("Cuenta desactivada. Contacta al administrador.");
        }

        // ✅ Generar token JWT real usando JwtService
        String token = jwtService.generateToken(user);

        log.info("Usuario autenticado exitosamente: {}", user.getEmail());

        // Construir respuesta
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tokenType("Bearer") // Explícito para claridad
                .build();
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return el usuario encontrado
     * @throws RuntimeException si no existe
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Verifica si un email ya está registrado.
     *
     * @param email el email a verificar
     * @return true si el email existe
     */
    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}