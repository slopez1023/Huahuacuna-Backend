package com.huahuacuna.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder con información de autenticación exitosa.
 * Utiliza el patrón Builder para facilitar la creación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private UserInfo user;

    /**
     * DTO anidado con información del usuario autenticado.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
    }
}