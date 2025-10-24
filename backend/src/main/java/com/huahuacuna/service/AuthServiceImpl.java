package com.huahuacuna.service;

import com.huahuacuna.exception.AuthenticationException;
import com.huahuacuna.exception.UserAlreadyExistsException;
import com.huahuacuna.model.*;
import com.huahuacuna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementación del servicio de autenticación para el sistema Huahuacuna.
 * <p>
 * Esta clase gestiona el proceso de inicio de sesión y registro de usuarios,
 * interactuando con la base de datos y aplicando las reglas de seguridad necesarias.
 * </p>
 *
 * <p>
 * Utiliza {@link UserRepository} para acceder a los datos de usuario,
 * y {@link PasswordEncoder} para el manejo seguro de contraseñas.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** Logger para registrar eventos y errores relacionados con la autenticación. */
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    /** Repositorio de usuarios utilizado para realizar operaciones en la base de datos. */
    private final UserRepository userRepository;

    /** Codificador de contraseñas utilizado para validar y encriptar contraseñas. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica a un usuario en el sistema a partir de sus credenciales.
     * <p>
     * Verifica la existencia del usuario en la base de datos y compara la contraseña
     * proporcionada con la almacenada. Si las credenciales son válidas, genera un token
     * de sesión simulado.
     * </p>
     *
     * @param loginRequest objeto que contiene el correo y contraseña del usuario.
     * @return un {@link LoginResponse} con el resultado del proceso de autenticación.
     * @throws AuthenticationException si las credenciales son incorrectas o los datos son inválidos.
     */
    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        logger.info("Intento de login para usuario: {}", loginRequest.getEmail());

        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            logger.warn("Solicitud de login inválida: datos nulos");
            throw new AuthenticationException("Solicitud de login inválida");
        }

        // Buscar usuario en la base de datos
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado: {}", loginRequest.getEmail());
                    return new AuthenticationException("Correo electrónico o contraseña incorrectos");
                });

        // Verificar contraseña usando BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Contraseña incorrecta para usuario: {}", loginRequest.getEmail());
            throw new AuthenticationException("Correo electrónico o contraseña incorrectos");
        }

        logger.info("✅ Login exitoso para usuario: {}", loginRequest.getEmail());

        // Generar token simulado
        String token = generateToken(user);

        return LoginResponse.builder()
                .success(true)
                .message("Inicio de sesión exitoso")
                .token(token)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getFullName())
                        .build())
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Antes de guardar el usuario, se valida que el correo no esté registrado
     * previamente y se encripta la contraseña con el {@link PasswordEncoder}.
     * </p>
     *
     * @param request objeto con los datos necesarios para el registro.
     * @return un {@link RegisterResponse} con información del resultado del registro.
     * @throws UserAlreadyExistsException si el correo electrónico ya está registrado.
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return RegisterResponse.error("Datos de registro incompletos");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("El usuario con email " + request.getEmail() + " ya existe");
        }

        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .build();

        // Guardar teléfono si se proporciona
        if (request.getTelefono() != null) {
            newUser.setTelefono(request.getTelefono());
        }

        User saved = userRepository.save(newUser);

        logger.info("✅ Usuario registrado exitosamente: {}", saved.getEmail());

        return RegisterResponse.success(saved);
    }

    /**
     * Genera un token simulado para representar una sesión activa de usuario.
     * <p>
     * En una implementación real, este método debería generar un JWT u otro
     * mecanismo de autenticación segura.
     * </p>
     *
     * @param user usuario autenticado.
     * @return un token de sesión en formato de cadena.
     */
    private String generateToken(User user) {
        return "bearer_" + UUID.randomUUID().toString();
    }
}
