package com.huahuacuna.controller;

import com.huahuacuna.model.Notification;
import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.NotificationDTO;
import com.huahuacuna.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de notificaciones del sistema.
 * Todas las operaciones requieren autenticación y rol ADMIN.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Obtiene todas las notificaciones del administrador autenticado.
     *
     * @param authentication Objeto de autenticación de Spring Security
     * @return Lista de todas las notificaciones del usuario
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyNotifications(Authentication authentication) {
        logger.info("GET /api/notifications - Obteniendo notificaciones del usuario");

        try {
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            List<Notification> notifications = notificationService.getNotificationsByUser(userId);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notificationDTOs);
            response.put("total", notificationDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener notificaciones", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene las notificaciones no leídas del administrador autenticado.
     *
     * @param authentication Objeto de autenticación
     * @return Lista de notificaciones no leídas
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(Authentication authentication) {
        logger.info("GET /api/notifications/unread - Obteniendo notificaciones no leídas");

        try {
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notificationDTOs);
            response.put("total", notificationDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener notificaciones no leídas", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cuenta las notificaciones no leídas del administrador autenticado.
     * Útil para mostrar badges de notificaciones en el frontend.
     *
     * @param authentication Objeto de autenticación
     * @return Número de notificaciones no leídas
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> countUnreadNotifications(Authentication authentication) {
        logger.info("GET /api/notifications/unread/count - Contando notificaciones no leídas");

        try {
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            long count = notificationService.countUnreadNotifications(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al contar notificaciones no leídas", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al contar las notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene una notificación específica por su ID.
     *
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNotificationById(@PathVariable Long id) {
        logger.info("GET /api/notifications/{} - Obteniendo notificación", id);

        try {
            Notification notification = notificationService.getNotificationById(id);
            NotificationDTO notificationDTO = new NotificationDTO(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notificationDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Notificación no encontrada: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Notificación no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Marca una notificación específica como leída.
     *
     * @param id ID de la notificación
     * @return Notificación actualizada
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        logger.info("PATCH /api/notifications/{}/read - Marcando como leída", id);

        try {
            Notification notification = notificationService.markAsRead(id);
            NotificationDTO notificationDTO = new NotificationDTO(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación marcada como leída");
            response.put("data", notificationDTO);

            logger.info("Notificación {} marcada como leída", id);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Notificación no encontrada: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Notificación no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al marcar notificación como leída", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar la notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Marca todas las notificaciones del usuario como leídas.
     *
     * @param authentication Objeto de autenticación
     * @return Respuesta de confirmación
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
        logger.info("PATCH /api/notifications/read-all - Marcando todas como leídas");

        try {
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            notificationService.markAllAsRead(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todas las notificaciones marcadas como leídas");

            logger.info("Todas las notificaciones del usuario {} marcadas como leídas", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al marcar todas las notificaciones como leídas", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar las notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Elimina una notificación específica.
     *
     * @param id ID de la notificación a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        logger.info("DELETE /api/notifications/{} - Eliminando notificación", id);

        try {
            notificationService.deleteNotification(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación eliminada exitosamente");

            logger.info("Notificación {} eliminada", id);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Notificación no encontrada: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Notificación no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al eliminar la notificación", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar la notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene notificaciones relacionadas con una solicitud específica.
     *
     * @param applicationId ID de la solicitud
     * @return Lista de notificaciones relacionadas
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Map<String, Object>> getNotificationsByApplication(@PathVariable Long applicationId) {
        logger.info("GET /api/notifications/application/{} - Obteniendo notificaciones de solicitud", applicationId);

        try {
            List<Notification> notifications = notificationService.getNotificationsByApplication(applicationId);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notificationDTOs);
            response.put("total", notificationDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener notificaciones de la solicitud", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}