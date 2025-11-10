package com.huahuacuna.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta exitosa de un login.
 * Contiene el token JWT y la información básica del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * Token JWT para autenticación en peticiones subsiguientes.
     */
    private String token;

    /**
     * ID del usuario autenticado.
     */
    private Long userId;

    /**
     * Email del usuario autenticado.
     */
    private String email;

    /**
     * Nombre completo del usuario.
     */
    private String fullName;

    /**
     * Rol del usuario en el sistema.
     */
    private Role role;

    /**
     * Tipo del token (generalmente "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";
}