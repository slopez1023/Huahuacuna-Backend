package com.huahuacuna.service;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.CreateUserDTO;
import com.huahuacuna.model.dto.UpdateUserDTO;
import com.huahuacuna.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de usuarios
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserDTO dto) {
        logger.info("Creando nuevo usuario: {}", dto.getEmail());

        // Verificar que el email no exista
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setTelefono(dto.getTelefono());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        logger.info("Usuario creado exitosamente con ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public User updateUser(Long id, UpdateUserDTO dto) {
        logger.info("Actualizando usuario con ID: {}", id);

        User user = getUserById(id);

        // Actualizar solo los campos que vienen en el DTO
        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            // Verificar que el nuevo email no esté en uso por otro usuario
            userRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(id)) {
                    throw new IllegalArgumentException("El email ya está en uso por otro usuario");
                }
            });
            user.setEmail(dto.getEmail());
        }

        if (dto.getTelefono() != null && !dto.getTelefono().trim().isEmpty()) {
            user.setTelefono(dto.getTelefono());
        }

        if (dto.getRole() != null && !dto.getRole().trim().isEmpty()) {
            user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        }

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());  // ✅ CORREGIDO
        }

        User updatedUser = userRepository.save(user);
        logger.info("Usuario actualizado exitosamente: {}", id);

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);

        User user = getUserById(id);

        // Verificar que no sea el último admin
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.getIsActive())
                    .count();

            if (adminCount <= 1) {
                throw new IllegalStateException("No se puede eliminar el último administrador del sistema");
            }
        }

        // Soft delete - marcar como inactivo en lugar de eliminar
        user.setIsActive(false);
        userRepository.save(user);

        logger.info("Usuario marcado como inactivo: {}", id);
    }

    @Override
    public User toggleUserStatus(Long id) {
        logger.info("Cambiando estado del usuario: {}", id);

        User user = getUserById(id);

        // Si se está desactivando un admin, verificar que no sea el último
        if (user.getIsActive() && user.getRole() == Role.ADMIN) {
            long activeAdminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.getIsActive())
                    .count();

            if (activeAdminCount <= 1) {
                throw new IllegalStateException("No se puede desactivar el último administrador activo");
            }
        }

        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);

        logger.info("Estado del usuario {} cambiado a: {}", id, updatedUser.getIsActive());
        return updatedUser;
    }

    @Override
    public void resetUserPassword(Long id, String newPassword) {
        logger.info("Reseteando contraseña del usuario: {}", id);

        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Contraseña reseteada exitosamente para el usuario: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        logger.info("Obteniendo usuarios con rol: {}", role);
        Role roleEnum = Role.valueOf(role.toUpperCase());
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == roleEnum)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        logger.info("Buscando usuarios con término: {}", searchTerm);
        String lowerSearchTerm = searchTerm.toLowerCase();

        return userRepository.findAll().stream()
                .filter(user ->
                        user.getFullName().toLowerCase().contains(lowerSearchTerm) ||
                                user.getEmail().toLowerCase().contains(lowerSearchTerm)
                )
                .toList();
    }
}