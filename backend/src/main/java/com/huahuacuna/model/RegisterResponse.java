package com.huahuacuna.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Objeto de transferencia de datos (DTO) utilizado como respuesta al registrar un usuario.
 * <p>
 * Esta clase encapsula la información que el servidor devuelve tras el proceso
 * de registro, indicando si fue exitoso, junto con los datos principales del nuevo usuario.
 * </p>
 *
 * <p>Ejemplo de respuesta exitosa (JSON):</p>
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "Usuario registrado correctamente",
 *   "userId": 15,
 *   "email": "usuario@ejemplo.com",
 *   "name": "Juan Pérez",
 *   "role": "USER",
 *   "createdAt": "2025-10-22T18:45:30"
 * }
 * }</pre>
 *
 * <p>Ejemplo de respuesta con error:</p>
 * <pre>{@code
 * {
 *   "success": false,
 *   "message": "El correo ya está registrado",
 *   "createdAt": "2025-10-22T18:46:10"
 * }
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    /**
     * Indica si el proceso de registro fue exitoso.
     */
    private boolean success;

    /**
     * Mensaje descriptivo del resultado del registro (éxito o error).
     */
    private String message;

    /**
     * Identificador único del usuario registrado.
     */
    private Long userId;

    /**
     * Correo electrónico del usuario recién registrado.
     */
    private String email;

    /**
     * Nombre completo del usuario.
     */
    private String name;

    /**
     * Rol asignado al usuario (por ejemplo, "USER" o "ADMIN").
     */
    private String role;

    /**
     * Fecha y hora en que se realizó el registro o se generó la respuesta.
     */
    private LocalDateTime createdAt;

    /**
     * Crea una instancia de {@code RegisterResponse} para una respuesta exitosa.
     * <p>
     * Este método de fábrica construye una respuesta con los datos del usuario recién creado.
     * </p>
     *
     * @param user el objeto {@link User} que contiene la información del usuario registrado.
     * @return un objeto {@code RegisterResponse} representando un registro exitoso.
     */
    public static RegisterResponse success(User user) {
        return RegisterResponse.builder()
                .success(true)
                .message("Usuario registrado correctamente")
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    /**
     * Crea una instancia de {@code RegisterResponse} para representar un error de registro.
     * <p>
     * Este método se utiliza cuando ocurre algún problema en el proceso de registro
     * (por ejemplo, un correo duplicado o datos inválidos).
     * </p>
     *
     * @param message mensaje descriptivo del error ocurrido.
     * @return un objeto {@code RegisterResponse} indicando un registro fallido.
     */
    public static RegisterResponse error(String message) {
        return RegisterResponse.builder()
                .success(false)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
