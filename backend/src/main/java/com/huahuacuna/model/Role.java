package com.huahuacuna.model;

import lombok.Getter;

/**
 * Enumeración que define los roles disponibles en el sistema.
 * <p>
 * - ADMIN: Administrador con acceso total al sistema
 * - VOLUNTARIO: Usuario voluntario que ha solicitado participar
 * - APADRINADO: Usuario que puede ser apadrinado/patrocinado
 * </p>
 */
@Getter
public enum Role {
    /**
     * Administrador del sistema con acceso completo
     */
    ADMIN("ROLE_ADMIN"),

    /**
     * Usuario voluntario
     */
    VOLUNTARIO("ROLE_VOLUNTARIO"),

    /**
     * Usuario apadrinado
     */
    APADRINADO("ROLE_APADRINADO");

    /**
     * -- GETTER --
     *  Obtiene el nombre de la autoridad con el prefijo ROLE_
     *  necesario para Spring Security.
     */
    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    /**
     * Convierte un string a Role, manejando tanto el formato
     * con prefijo ROLE_ como sin él.
     */
    public static Role fromString(String role) {
        if (role == null) {
            return APADRINADO; // Rol por defecto
        }

        // Remover el prefijo ROLE_ si existe
        String cleanRole = role.replace("ROLE_", "");

        try {
            return Role.valueOf(cleanRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            return APADRINADO; // Rol por defecto si no se encuentra
        }
    }
}