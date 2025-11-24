package com.huahuacuna.model.dto;

import com.huahuacuna.model.Notification;

import java.time.LocalDateTime;

/**
 * DTO para transferir información de notificaciones al frontend.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Long applicationId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Constructor vacío
    public NotificationDTO() {
    }

    /**
     * Constructor desde entidad Notification
     *
     * @param notification Entidad de notificación
     */
    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.isRead = notification.getIsRead();
        this.applicationId = notification.getApplicationId();
        this.createdAt = notification.getCreatedAt();
        this.readAt = notification.getReadAt();
    }

    // Getters y Setters
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
}