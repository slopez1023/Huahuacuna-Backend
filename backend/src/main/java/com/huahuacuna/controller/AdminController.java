package com.huahuacuna.controller;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import com.huahuacuna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades exclusivas del administrador.
 * <p>
 * Permite al admin gestionar usuarios, revisar solicitudes y
 * administrar el sistema completo.
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return lista de todos los usuarios
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Admin consultando todos los usuarios. Total: {}", users.size());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", users.size(),
                "users", users
        ));
    }

    /**
     * Obtiene todas las solicitudes de voluntarios.
     *
     * @return lista de usuarios con rol VOLUNTARIO
     */
    @GetMapping("/voluntarios")
    public ResponseEntity<?> getVoluntarios() {
        List<User> voluntarios = userRepository.findByRole(Role.VOLUNTARIO);
        log.info("Admin consultando voluntarios. Total: {}", voluntarios.size());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", voluntarios.size(),
                "voluntarios", voluntarios
        ));
    }

    /**
     * Obtiene todas las solicitudes de apadrinados.
     *
     * @return lista de usuarios con rol APADRINADO
     */
    @GetMapping("/apadrinados")
    public ResponseEntity<?> getApadrinados() {
        List<User> apadrinados = userRepository.findByRole(Role.APADRINADO);
        log.info("Admin consultando apadrinados. Total: {}", apadrinados.size());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", apadrinados.size(),
                "apadrinados", apadrinados
        ));
    }

    /**
     * Activa o desactiva una cuenta de usuario.
     *
     * @param userId   ID del usuario a modificar
     * @param isActive nuevo estado de activación
     * @return confirmación de la operación
     */
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setIsActive(isActive);
        userRepository.save(user);

        log.info("Admin cambió estado de usuario {} a {}", userId, isActive);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Estado actualizado correctamente",
                "user", user
        ));
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param userId ID del usuario a eliminar
     * @return confirmación de la eliminación
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(userId);
        log.warn("Admin eliminó usuario con ID: {}", userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Usuario eliminado correctamente"
        ));
    }

    /**
     * Obtiene estadísticas generales del sistema.
     *
     * @return conteo de usuarios por rol
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findByRole(Role.ADMIN).size();
        long totalVoluntarios = userRepository.findByRole(Role.VOLUNTARIO).size();
        long totalApadrinados = userRepository.findByRole(Role.APADRINADO).size();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", Map.of(
                        "totalUsers", totalUsers,
                        "admins", totalAdmins,
                        "voluntarios", totalVoluntarios,
                        "apadrinados", totalApadrinados
                )
        ));
    }
}