package com.huahuacuna.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar un usuario existente
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class UpdateUserDTO {

    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String fullName;

    @Email(message = "Debe proporcionar un email válido")
    private String email;

    private String telefono;

    @Pattern(
            regexp = "^(ADMIN|VOLUNTARIO|PADRINO)$",
            message = "El rol debe ser: ADMIN, VOLUNTARIO o PADRINO"
    )
    private String role; // ADMIN, VOLUNTARIO, PADRINO

    private Boolean isActive;

    // Constructores
    public UpdateUserDTO() {
    }

    public UpdateUserDTO(String fullName, String email, String telefono, String role, Boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.telefono = telefono;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters y Setters
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}