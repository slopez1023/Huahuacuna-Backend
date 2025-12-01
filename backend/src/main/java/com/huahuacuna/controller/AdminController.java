package com.huahuacuna.controller;

import com.huahuacuna.model.Role;
import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.LogEntryDTO;
import com.huahuacuna.service.GodparentService;
import com.huahuacuna.repository.UserRepository;
import com.huahuacuna.model.dto.SponsorshipSummaryDTO;
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
    private final GodparentService godparentService;

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
     * Obtiene todas las solicitudes de padrinos.
     *
     * @return lista de usuarios con rol PADRINO
     */
    @GetMapping("/padrinos")
    public ResponseEntity<?> getPadrinos() {
        List<User> padrinos = userRepository.findByRole(Role.PADRINO);
        log.info("Admin consultando padrinos. Total: {}", padrinos.size());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", padrinos.size(),
                "padrinos", padrinos
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
    // ========== BITÁCORA (SOLO ADMIN) ==========

    /**
     * Agrega una entrada a la bitácora de un niño apadrinado.
     * POST /api/admin/apadrinamientos/{id}/bitacora
     *
     * Solo el administrador puede agregar entradas a la bitácora.
     * El padrino solo puede leer las entradas.
     *
     * @param sponsorshipId ID del apadrinamiento
     * @param body Contenido de la entrada (titulo, contenido)
     * @return LogEntryDTO con la entrada creada
     */
    @PostMapping("/apadrinamientos/{id}/bitacora")
    public ResponseEntity<?> addLogEntry(
            @PathVariable("id") Long sponsorshipId,
            @RequestBody Map<String, String> body
    ) {
        String titulo = body.get("titulo");
        String contenido = body.get("contenido");

        log.info("POST /api/admin/apadrinamientos/{}/bitacora - Admin agregando entrada", sponsorshipId);

        if (titulo == null || titulo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El título es obligatorio"
            ));
        }

        if (contenido == null || contenido.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El contenido es obligatorio"
            ));
        }

        try {
            // Llamar al servicio para crear la entrada
            // NOTA: Debes tener un método en tu servicio para esto
            LogEntryDTO entry = godparentService.addLogEntryByAdmin(sponsorshipId, titulo, contenido);

            log.info("Entrada de bitácora creada exitosamente para apadrinamiento {}", sponsorshipId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Entrada agregada exitosamente",
                    "entry", entry
            ));
        } catch (RuntimeException e) {
            log.error("Error al agregar entrada a bitácora: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Obtiene todas las entradas de bitácora de un apadrinamiento (vista admin).
     * GET /api/admin/apadrinamientos/{id}/bitacora
     */
    @GetMapping("/apadrinamientos/{id}/bitacora")
    public ResponseEntity<?> getLogEntriesAdmin(@PathVariable("id") Long sponsorshipId) {
        log.info("GET /api/admin/apadrinamientos/{}/bitacora", sponsorshipId);

        try {
            List<LogEntryDTO> entries = godparentService.getLogEntriesAdmin(sponsorshipId);
            return ResponseEntity.ok(entries);
        } catch (RuntimeException e) {
            log.error("Error al obtener bitácora: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Obtiene todos los apadrinamientos activos para la gestión de bitácoras.
     * GET /api/admin/apadrinamientos
     *
     * @return Lista de apadrinamientos activos con información del niño y padrino
     */
    @GetMapping("/apadrinamientos")
    public ResponseEntity<?> getActiveSponsorships() {
        log.info("GET /api/admin/apadrinamientos - Obteniendo apadrinamientos activos");

        try {
            List<SponsorshipSummaryDTO> sponsorships = godparentService.getAllActiveSponsorshipsForAdmin();
            return ResponseEntity.ok(sponsorships);
        } catch (RuntimeException e) {
            log.error("Error al obtener apadrinamientos: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
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
        long totalPadrinos = userRepository.findByRole(Role.PADRINO).size();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", Map.of(
                        "totalUsers", totalUsers,
                        "admins", totalAdmins,
                        "voluntarios", totalVoluntarios,
                        "padrinos", totalPadrinos
                )
        ));
    }
}