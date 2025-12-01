package com.huahuacuna.controller;

import com.huahuacuna.model.dto.*;
import com.huahuacuna.service.GodparentService;
import com.huahuacuna.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades de padrinos.
 * Base path: /api/padrinos
 *
 * CORRECCIÓN v2.0: Removido el endpoint de agregar entradas a la bitácora.
 * El padrino solo puede LEER la bitácora, no modificarla.
 * La creación de entradas es exclusiva del administrador.
 *
 * @author Fundación Huahuacuna
 * @version 2.0 - Solo lectura de bitácora
 */
@RestController
@RequestMapping("/api/padrinos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class GodparentController {

    private final GodparentService godparentService;
    private final JwtService jwtService;

    // ========== PERFIL DEL PADRINO ==========

    /**
     * Obtiene el perfil del padrino actual.
     * GET /api/padrinos/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<GodparentProfileDTO> getMyProfile(HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        log.info("GET /api/padrinos/me - Usuario: {}", userId);

        GodparentProfileDTO profile = godparentService.getMyProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // ========== NIÑOS DISPONIBLES ==========

    /**
     * Obtiene la lista de niños disponibles para apadrinar.
     * GET /api/padrinos/children/available
     */
    @GetMapping("/children/available")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<List<ChildResponseDTO>> getAvailableChildren() {
        log.info("GET /api/padrinos/children/available");

        List<ChildResponseDTO> children = godparentService.getAvailableChildren();
        return ResponseEntity.ok(children);
    }

    // ========== APADRINAMIENTO ==========

    /**
     * Selecciona un niño para apadrinar.
     * POST /api/padrinos/select-child
     */
    @PostMapping("/select-child")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<?> selectChild(
            @Valid @RequestBody SelectChildRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("POST /api/padrinos/select-child - Usuario: {}, Niño: {}", userId, request.getIdNino());

        try {
            SponsorshipResponseDTO sponsorship = godparentService.selectChild(userId, request.getIdNino());
            return ResponseEntity.ok(sponsorship);
        } catch (RuntimeException e) {
            log.error("Error al seleccionar niño: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene el apadrinamiento activo del padrino.
     * GET /api/padrinos/my-godchild
     */
    @GetMapping("/my-godchild")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<?> getMyGodchild(HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        log.info("GET /api/padrinos/my-godchild - Usuario: {}", userId);

        SponsorshipResponseDTO sponsorship = godparentService.getMyGodchild(userId);

        if (sponsorship == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(sponsorship);
    }

    // ========== BITÁCORA (SOLO LECTURA) ==========

    /**
     * Obtiene las entradas de bitácora de un apadrinamiento.
     * GET /api/padrinos/apadrinamientos/{id}/bitacora
     *
     * NOTA: El padrino SOLO puede leer la bitácora, NO agregar entradas.
     * La creación de entradas es exclusiva del administrador.
     */
    @GetMapping("/apadrinamientos/{id}/bitacora")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<?> getLogEntries(
            @PathVariable("id") Long sponsorshipId,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromToken(request);
        log.info("GET /api/padrinos/apadrinamientos/{}/bitacora - Usuario: {}", sponsorshipId, userId);

        try {
            List<LogEntryDTO> entries = godparentService.getLogEntries(sponsorshipId, userId);
            return ResponseEntity.ok(entries);
        } catch (RuntimeException e) {
            log.error("Error al obtener bitácora: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * ❌ REMOVIDO: Endpoint para agregar entrada a la bitácora
     *
     * Este endpoint ha sido ELIMINADO porque el padrino NO debe poder
     * agregar entradas a la bitácora. Esta funcionalidad es EXCLUSIVA
     * del administrador y debe implementarse en AdminController.
     *
     * El endpoint correspondiente en el admin sería:
     * POST /api/admin/apadrinamientos/{id}/bitacora
     *
     * @see AdminController#addLogEntry
     */
    // @PostMapping("/apadrinamientos/{id}/bitacora") - REMOVIDO

    // ========== CHAT ==========

    /**
     * Obtiene los mensajes del chat de un apadrinamiento.
     * GET /api/padrinos/apadrinamientos/{id}/mensajes
     */
    @GetMapping("/apadrinamientos/{id}/mensajes")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<?> getChatMessages(
            @PathVariable("id") Long sponsorshipId,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromToken(request);
        log.info("GET /api/padrinos/apadrinamientos/{}/mensajes - Usuario: {}", sponsorshipId, userId);

        try {
            List<ChatMessageDTO> messages = godparentService.getChatMessages(sponsorshipId, userId);

            // Marcar mensajes del admin como leídos
            godparentService.markMessagesAsRead(sponsorshipId, userId);

            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            log.error("Error al obtener mensajes: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Envía un mensaje al administrador.
     * POST /api/padrinos/apadrinamientos/{id}/mensajes
     */
    @PostMapping("/apadrinamientos/{id}/mensajes")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<?> sendMessage(
            @PathVariable("id") Long sponsorshipId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromToken(request);
        String contenido = body.get("contenido");

        log.info("POST /api/padrinos/apadrinamientos/{}/mensajes - Usuario: {}", sponsorshipId, userId);

        if (contenido == null || contenido.isBlank()) {
            return ResponseEntity.badRequest().body(createErrorResponse("El mensaje no puede estar vacío"));
        }

        try {
            ChatMessageDTO message = godparentService.sendMessage(sponsorshipId, userId, contenido);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            log.error("Error al enviar mensaje: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene el contador de mensajes no leídos.
     * GET /api/padrinos/mensajes/no-leidos
     */
    @GetMapping("/mensajes/no-leidos")
    @PreAuthorize("hasRole('PADRINO')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        log.info("GET /api/padrinos/mensajes/no-leidos - Usuario: {}", userId);

        long count = godparentService.countUnreadMessages(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Extrae el ID del usuario del token JWT.
     */
    private Long extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }

        String token = authHeader.substring(7);
        return jwtService.getUserIdFromToken(token);
    }

    /**
     * Crea una respuesta de error estandarizada.
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}