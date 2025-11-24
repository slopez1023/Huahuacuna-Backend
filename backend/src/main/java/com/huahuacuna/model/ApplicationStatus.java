package com.huahuacuna.model;

/**
 * Enumeración que define los posibles estados de una solicitud
 * de participación en el sistema.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public enum ApplicationStatus {
    /**
     * Solicitud recién creada, pendiente de revisión
     */
    PENDIENTE,

    /**
     * Solicitud en proceso de revisión por el administrador
     */
    EN_REVISION,

    /**
     * Solicitud aprobada por el administrador
     */
    APROBADO,

    /**
     * Solicitud rechazada por el administrador
     */
    RECHAZADO
}