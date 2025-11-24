package com.huahuacuna.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un nuevo usuario desde el panel administrativo
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public class CreateUserDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "^(ADMIN|VOLUNTARIO|PADRINO)$",
            message = "El rol debe ser: ADMIN, VOLUNTARIO o PADRINO"
    )
    private String role; // ADMIN, VOLUNTARIO, PADRINO

    // Constructores
    public CreateUserDTO() {
    }

    public CreateUserDTO(String fullName, String email, String telefono, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}