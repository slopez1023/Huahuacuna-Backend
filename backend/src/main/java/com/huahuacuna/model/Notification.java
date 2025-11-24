package com.huahuacuna.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación para el administrador.
 * Se genera automáticamente cuando hay nuevas solicitudes o eventos importantes.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título o asunto de la notificación
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Mensaje detallado de la notificación
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Tipo de notificación (INFO, WARNING, SUCCESS, ERROR)
     */
    @Column(nullable = false, length = 20)
    private String type;

    /**
     * Indica si la notificación ha sido leída
     */
    @Column(nullable = false)
    private Boolean isRead = false;

    /**
     * ID del usuario administrador que debe recibir la notificación
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * ID de la solicitud relacionada (si aplica)
     */
    @Column
    private Long applicationId;

    /**
     * Fecha y hora de creación de la notificación
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora en que se leyó la notificación
     */
    @Column
    private LocalDateTime readAt;

    // ========== MÉTODOS DE CICLO DE VIDA JPA ==========

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
    }

    // ========== CONSTRUCTORES ==========

    public Notification() {
    }

    /**
     * Constructor completo para crear notificaciones
     */
    public Notification(String title, String message, String type, Long userId, Long applicationId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.applicationId = applicationId;
        this.isRead = false;
    }

    // ========== GETTERS Y SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    /**
     * Marca la notificación como leída
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}