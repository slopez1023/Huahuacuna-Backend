package com.huahuacuna.config;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import com.huahuacuna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente que inicializa datos por defecto en la base de datos.
 * <p>
 * Se ejecuta automáticamente al arrancar la aplicación y crea
 * el usuario administrador si no existe.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Credenciales por defecto del administrador.
     * IMPORTANTE: Cambiar estas credenciales en producción.
     */
    private static final String ADMIN_EMAIL = "admin@huahuacuna.org";
    private static final String ADMIN_PASSWORD = "Admin@2025";
    private static final String ADMIN_NAME = "Administrador Sistema";

    @Override
    public void run(String... args) {
        createDefaultAdmin();
    }

    /**
     * Crea el usuario administrador por defecto si no existe.
     */
    private void createDefaultAdmin() {
        try {
            // Verificar si ya existe un administrador
            if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {

                User admin = User.builder()
                        .fullName(ADMIN_NAME)
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .role(Role.ADMIN)
                        .telefono(null)
                        .isActive(true)
                        .build();

                userRepository.save(admin);

                log.info("========================================");
                log.info("✅ Usuario administrador creado exitosamente");
                log.info("📧 Email: {}", ADMIN_EMAIL);
                log.info("🔑 Password: {}", ADMIN_PASSWORD);
                log.info("⚠️  IMPORTANTE: Cambia esta contraseña después del primer login");
                log.info("========================================");
            } else {
                log.info("ℹ️  Usuario administrador ya existe en la base de datos");
            }
        } catch (Exception e) {
            log.error("❌ Error al crear usuario administrador: {}", e.getMessage(), e);
        }
    }
}