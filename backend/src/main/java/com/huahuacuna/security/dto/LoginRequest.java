

package com.huahuacuna.security.dto;

// Usamos record para DTOs simples
public record LoginRequest(
        String email,
        String password
) {}
