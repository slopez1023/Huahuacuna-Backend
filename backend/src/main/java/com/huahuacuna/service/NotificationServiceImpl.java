package com.huahuacuna.service;

import com.huahuacuna.model.Notification;
import com.huahuacuna.model.Role;  // ✅ IMPORTAR
import com.huahuacuna.model.User;
import com.huahuacuna.repository.NotificationRepository;
import com.huahuacuna.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de gestión de notificaciones.
 * Maneja toda la lógica relacionada con notificaciones del sistema.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification createNotification(String title, String message, String type, Long userId, Long applicationId) {
        logger.info("Creando notificación para usuario {}: {}", userId, title);

        Notification notification = new Notification(title, message, type, userId, applicationId);
        Notification savedNotification = notificationRepository.save(notification);

        logger.info("Notificación creada con ID: {}", savedNotification.getId());
        return savedNotification;
    }

    /**
     * ✅ NUEVO MÉTODO: Crear notificaciones para todos los administradores
     */
    @Override
    public List<Notification> createNotificationForAllAdmins(String title, String message, String type, Long relatedEntityId) {
        logger.info("Creando notificación para todos los administradores: {}", title);

        List<Notification> createdNotifications = new ArrayList<>();

        try {
            // ✅ Obtener todos los usuarios con rol ADMIN (usando el enum)
            List<User> admins = userRepository.findByRole(Role.ADMIN);

            if (admins.isEmpty()) {
                logger.warn("No se encontraron administradores para enviar la notificación");
                return createdNotifications;
            }

            logger.info("Se encontraron {} administradores", admins.size());

            // Crear una notificación para cada administrador
            for (User admin : admins) {
                Notification notification = new Notification(title, message, type, admin.getId(), relatedEntityId);
                Notification savedNotification = notificationRepository.save(notification);
                createdNotifications.add(savedNotification);
                logger.debug("Notificación creada para admin {} ({}): ID {}",
                        admin.getFullName(), admin.getEmail(), savedNotification.getId());
            }

            logger.info("✅ Se crearon {} notificaciones para administradores", createdNotifications.size());
        } catch (Exception e) {
            logger.error("❌ Error al crear notificaciones para administradores", e);
            throw new RuntimeException("Error al crear notificaciones para administradores", e);
        }

        return createdNotifications;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        logger.info("Buscando notificación con ID: {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Notificación no encontrada con ID: {}", id);
                    return new RuntimeException("Notificación no encontrada con ID: " + id);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(Long userId) {
        logger.info("Obteniendo todas las notificaciones del usuario: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        logger.info("Obteniendo notificaciones no leídas del usuario: {}", userId);
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        logger.info("Contando notificaciones no leídas del usuario: {}", userId);
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification markAsRead(Long id) {
        logger.info("Marcando notificación {} como leída", id);

        Notification notification = getNotificationById(id);
        notification.markAsRead();

        Notification updatedNotification = notificationRepository.save(notification);
        logger.info("Notificación {} marcada como leída", id);

        return updatedNotification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markAllAsRead(Long userId) {
        logger.info("Marcando todas las notificaciones como leídas para el usuario: {}", userId);
        notificationRepository.markAllAsReadByUserId(userId);
        logger.info("Todas las notificaciones del usuario {} marcadas como leídas", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(Long id) {
        logger.info("Eliminando notificación: {}", id);

        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);

        logger.info("Notificación {} eliminada", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanOldNotifications() {
        logger.info("Limpiando notificaciones antiguas leídas");
        try {
            // Calcular fecha límite (30 días atrás)
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

            notificationRepository.deleteOldReadNotifications(cutoffDate);
            logger.info("Notificaciones antiguas eliminadas exitosamente");
        } catch (Exception e) {
            logger.error("Error al limpiar notificaciones antiguas", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByApplication(Long applicationId) {
        logger.info("Obteniendo notificaciones relacionadas con la solicitud: {}", applicationId);
        return notificationRepository.findByApplicationId(applicationId);
    }
}