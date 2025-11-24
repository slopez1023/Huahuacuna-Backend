package com.huahuacuna.service;

import com.huahuacuna.model.Notification;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de notificaciones del sistema.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public interface NotificationService {

    /**
     * Crea una nueva notificación
     *
     * @param title Título de la notificación
     * @param message Mensaje de la notificación
     * @param type Tipo de notificación (INFO, WARNING, SUCCESS, ERROR)
     * @param userId ID del usuario destinatario
     * @param applicationId ID de la solicitud relacionada (opcional)
     * @return Notificación creada
     */
    Notification createNotification(String title, String message, String type, Long userId, Long applicationId);

    // ✅ NUEVO MÉTODO: Crear notificación para todos los administradores
    /**
     * Crea notificaciones para todos los usuarios administradores
     *
     * @param title Título de la notificación
     * @param message Mensaje de la notificación
     * @param type Tipo de notificación
     * @param relatedEntityId ID de la entidad relacionada (donación, etc)
     * @return Lista de notificaciones creadas
     */
    List<Notification> createNotificationForAllAdmins(String title, String message, String type, Long relatedEntityId);

    /**
     * Obtiene una notificación por su ID
     *
     * @param id ID de la notificación
     * @return Notificación encontrada
     * @throws RuntimeException si no existe
     */
    Notification getNotificationById(Long id);

    /**
     * Obtiene todas las notificaciones de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de notificaciones ordenadas por fecha
     */
    List<Notification> getNotificationsByUser(Long userId);

    /**
     * Obtiene las notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @return Número de notificaciones no leídas
     */
    long countUnreadNotifications(Long userId);

    /**
     * Marca una notificación como leída
     *
     * @param id ID de la notificación
     * @return Notificación actualizada
     */
    Notification markAsRead(Long id);

    /**
     * Marca todas las notificaciones de un usuario como leídas
     *
     * @param userId ID del usuario
     */
    void markAllAsRead(Long userId);

    /**
     * Elimina una notificación
     *
     * @param id ID de la notificación
     */
    void deleteNotification(Long id);

    /**
     * Elimina notificaciones antiguas leídas (más de 30 días)
     * Este método puede ejecutarse periódicamente con un scheduler
     */
    void cleanOldNotifications();

    /**
     * Obtiene notificaciones relacionadas con una solicitud específica
     *
     * @param applicationId ID de la solicitud
     * @return Lista de notificaciones relacionadas
     */
    List<Notification> getNotificationsByApplication(Long applicationId);
}