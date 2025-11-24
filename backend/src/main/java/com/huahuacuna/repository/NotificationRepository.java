package com.huahuacuna.repository;

import com.huahuacuna.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la gestión de notificaciones del sistema.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Busca todas las notificaciones de un usuario específico
     * ordenadas por fecha de creación (más recientes primero)
     *
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Busca notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @param isRead Estado de lectura (false para no leídas)
     * @return Lista de notificaciones no leídas
     */
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @param isRead Estado de lectura (false para contar no leídas)
     * @return Número de notificaciones no leídas
     */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    /**
     * Busca notificaciones relacionadas con una solicitud específica
     *
     * @param applicationId ID de la solicitud
     * @return Lista de notificaciones relacionadas
     */
    List<Notification> findByApplicationId(Long applicationId);

    /**
     * Marca todas las notificaciones de un usuario como leídas
     *
     * @param userId ID del usuario
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);

    /**
     * Elimina notificaciones antiguas leídas (más de 30 días)
     *
     * @param cutoffDate Fecha límite (notificaciones anteriores a esta fecha serán eliminadas)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.readAt < :cutoffDate")
    void deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}