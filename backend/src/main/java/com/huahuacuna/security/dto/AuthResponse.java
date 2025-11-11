
package com.huahuacuna.security.dto;

// Cambiamos el record para que coincida con lo que el frontend espera
// (token + info del usuario)
public record AuthResponse(
        String accessToken,
        UsuarioDto usuario
) {}