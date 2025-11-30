package com.huahuacuna.model;

/**
 * Estados posibles de un apadrinamiento.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public enum SponsorshipStatus {

    /**
     * Apadrinamiento activo - El padrino está vinculado con el niño
     */
    ACTIVE("Activo"),

    /**
     * Apadrinamiento pausado - Temporalmente suspendido
     */
    PAUSED("Pausado"),

    /**
     * Apadrinamiento finalizado - Ya no está vigente
     */
    ENDED("Finalizado");

    private final String displayName;

    SponsorshipStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}