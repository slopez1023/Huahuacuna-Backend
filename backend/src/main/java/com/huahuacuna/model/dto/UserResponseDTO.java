package com.huahuacuna.model.dto;

import com.huahuacuna.model.User;
import java.time.LocalDateTime;

/**
 * DTO para enviar información de usuario al frontend
 * No expone información sensible como contraseñas
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String telefono;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

    // Constructor vacío
    public UserResponseDTO() {
    }

    /**
     * Constructor desde entidad User
     */
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.telefono = user.getTelefono();
        this.role = user.getRole().name();
        this.active = user.getIsActive();
        this.createdAt = user.getCreatedAt();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}