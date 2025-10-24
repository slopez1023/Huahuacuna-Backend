package com.huahuacuna.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de transferencia de datos (DTO) utilizado para el registro de nuevos usuarios.
 * <p>
 * Esta clase define los campos requeridos para crear una cuenta, junto con las
 * validaciones necesarias para garantizar que los datos sean válidos antes de
 * procesar la solicitud en el backend.
 * </p>
 *
 * <p>Ejemplo de uso (JSON en una solicitud HTTP):</p>
 * <pre>{@code
 * {
 *   "fullName": "Juan Pérez",
 *   "email": "juan.perez@ejemplo.com",
 *   "password": "MiContraseña123",
 *   "role": "USER",
 *   "telefono": "3216549870"
 * }
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Nombre completo del usuario.
     * <p>
     * Debe tener entre 3 y 100 caracteres y no puede estar vacío.
     * </p>
     */
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String fullName;

    /**
     * Correo electrónico del usuario.
     * <p>
     * Debe tener un formato válido y no puede estar vacío.
     * </p>
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Contraseña del usuario.
     * <p>
     * Debe tener al menos 6 caracteres y no puede estar vacía.
     * </p>
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener mínimo 6 caracteres")
    private String password;

    /**
     * Rol asignado al usuario dentro del sistema.
     * <p>
     * Por defecto, se asigna el valor "USER" al registrarse.
     * </p>
     */
    private String role = "USER";

    /**
     * Número de teléfono del usuario.
     * <p>
     * Este campo es opcional, pero no debe superar los 10 caracteres.
     * </p>
     */
    @Size(max = 10, message = "El teléfono no debe superar los 10 caracteres")
    private String telefono;
}
