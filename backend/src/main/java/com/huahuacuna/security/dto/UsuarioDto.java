
package com.huahuacuna.security.dto;

import java.util.Set;

// Usamos record para un DTO simple
public record UsuarioDto(
        Long id,
        String email,
        String nombre,
        Set<String> roles
) {}