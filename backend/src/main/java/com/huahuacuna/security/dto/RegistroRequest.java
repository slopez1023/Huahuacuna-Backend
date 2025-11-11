

package com.huahuacuna.security.dto;

public record RegistroRequest(
        String nombre,
        String email,
        String password
) {}