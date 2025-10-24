package com.huahuacuna.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de transferencia de datos (DTO) utilizado para recibir
 * las credenciales de inicio de sesión de un usuario.
 * <p>
 * Esta clase valida los campos de entrada antes de procesar la autenticación,
 * garantizando que el correo electrónico y la contraseña cumplan con los
 * requisitos mínimos.
 * </p>
 *
 * <p>Ejemplo de uso (JSON en una solicitud HTTP):</p>
 * <pre>{@code
 * {
 *   "email": "usuario@ejemplo.com",
 *   "password": "MiContraseña123"
 * }
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Correo electrónico del usuario.
     * <p>
     * Debe ser un formato válido y no puede estar vacío.
     * </p>
     */
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Por favor, ingresa un correo electrónico válido")
    private String email;

    /**
     * Contraseña asociada a la cuenta del usuario.
     * <p>
     * Este campo no puede estar vacío y debe cumplir con los
     * criterios mínimos definidos en la lógica del sistema.
     * </p>
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
