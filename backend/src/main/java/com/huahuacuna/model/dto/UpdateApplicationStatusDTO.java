package com.huahuacuna.model.dto;

import com.huahuacuna.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado de una solicitud.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class UpdateApplicationStatusDTO {

    @NotNull(message = "El estado es obligatorio")
    private ApplicationStatus status;

    private String comments;

    // Constructor vacío
    public UpdateApplicationStatusDTO() {
    }

    public UpdateApplicationStatusDTO(ApplicationStatus status, String comments) {
        this.status = status;
        this.comments = comments;
    }

    // Getters y Setters
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}