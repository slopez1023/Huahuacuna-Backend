package com.huahuacuna.controller;

import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.CreateUserDTO;
import com.huahuacuna.model.dto.UpdateUserDTO;
import com.huahuacuna.model.dto.UserResponseDTO;
import com.huahuacuna.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de usuarios (solo ADMIN)
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crea un nuevo usuario
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserDTO dto) {
        logger.info("POST /api/users - Creando nuevo usuario");

        try {
            User user = userService.createUser(dto);
            UserResponseDTO responseDTO = new UserResponseDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            response.put("data", responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al crear usuario", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene todos los usuarios
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        logger.info("GET /api/users - Obteniendo todos los usuarios");

        try {
            List<User> users = userService.getAllUsers();
            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener usuarios", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener los usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene un usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Obteniendo usuario", id);

        try {
            User user = userService.getUserById(id);
            UserResponseDTO responseDTO = new UserResponseDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Usuario no encontrado: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Actualiza un usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto) {

        logger.info("PUT /api/users/{} - Actualizando usuario", id);

        try {
            User user = userService.updateUser(id, dto);
            UserResponseDTO responseDTO = new UserResponseDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado exitosamente");
            response.put("data", responseDTO);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            logger.warn("Usuario no encontrado: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Elimina un usuario (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Eliminando usuario", id);

        try {
            userService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            logger.warn("No se puede eliminar el usuario: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            logger.warn("Usuario no encontrado: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Activa o desactiva un usuario
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long id) {
        logger.info("PATCH /api/users/{}/toggle-status - Cambiando estado", id);

        try {
            User user = userService.toggleUserStatus(id);
            UserResponseDTO responseDTO = new UserResponseDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado del usuario actualizado");
            response.put("data", responseDTO);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            logger.warn("No se puede cambiar el estado: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            logger.warn("Usuario no encontrado: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Resetea la contraseña de un usuario
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        logger.info("PATCH /api/users/{}/reset-password - Reseteando contraseña", id);

        try {
            String newPassword = request.get("newPassword");

            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            userService.resetUserPassword(id, newPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contraseña reseteada exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            logger.warn("Usuario no encontrado: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Busca usuarios por término de búsqueda
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String q) {
        logger.info("GET /api/users/search?q={}", q);

        try {
            List<User> users = userService.searchUsers(q);
            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al buscar usuarios", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al buscar usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene usuarios por rol
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String role) {
        logger.info("GET /api/users/role/{}", role);

        try {
            List<User> users = userService.getUsersByRole(role);
            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener usuarios por rol", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}