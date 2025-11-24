package com.huahuacuna.service;

import com.huahuacuna.model.ApplicationRequest;
import com.huahuacuna.model.ApplicationStatus;
import com.huahuacuna.model.ApplicationType;

import java.util.List;
import java.util.Map;

/**
 * Interfaz de servicio para la gestión de solicitudes de participación.
 * Define los métodos de negocio disponibles.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
public interface ApplicationService {

    /**
     * Crea una nueva solicitud de voluntariado
     *
     * @param applicationRequest Datos de la solicitud
     * @return Solicitud creada con ID generado
     * @throws IllegalArgumentException si el email ya existe
     */
    ApplicationRequest createVolunteerApplication(ApplicationRequest applicationRequest);

    /**
     * Crea una nueva solicitud de apadrinamiento
     *
     * @param applicationRequest Datos de la solicitud
     * @return Solicitud creada con ID generado
     * @throws IllegalArgumentException si el email ya existe
     */
    ApplicationRequest createSponsorApplication(ApplicationRequest applicationRequest);

    /**
     * Obtiene una solicitud por su ID
     *
     * @param id ID de la solicitud
     * @return Solicitud encontrada
     * @throws RuntimeException si no existe
     */
    ApplicationRequest getApplicationById(Long id);

    /**
     * Obtiene todas las solicitudes
     *
     * @return Lista de todas las solicitudes
     */
    List<ApplicationRequest> getAllApplications();

    /**
     * Obtiene solicitudes filtradas por tipo
     *
     * @param type Tipo de solicitud (VOLUNTARIO o PADRINO)
     * @return Lista de solicitudes del tipo especificado
     */
    List<ApplicationRequest> getApplicationsByType(ApplicationType type);

    /**
     * Obtiene solicitudes filtradas por estado
     *
     * @param status Estado de la solicitud
     * @return Lista de solicitudes con ese estado
     */
    List<ApplicationRequest> getApplicationsByStatus(ApplicationStatus status);

    /**
     * Obtiene todas las solicitudes pendientes
     *
     * @return Lista de solicitudes pendientes ordenadas por fecha
     */
    List<ApplicationRequest> getPendingApplications();

    /**
     * Actualiza el estado de una solicitud
     *
     * @param id ID de la solicitud
     * @param status Nuevo estado
     * @param adminId ID del administrador que realiza la acción
     * @param comments Comentarios del administrador (opcional)
     * @return Solicitud actualizada
     */
    ApplicationRequest updateApplicationStatus(Long id, ApplicationStatus status, Long adminId, String comments);

    /**
     * Aprueba una solicitud
     *
     * @param id ID de la solicitud
     * @param adminId ID del administrador
     * @param comments Comentarios opcionales
     * @return Solicitud aprobada
     */
    ApplicationRequest approveApplication(Long id, Long adminId, String comments);

    /**
     * Rechaza una solicitud
     *
     * @param id ID de la solicitud
     * @param adminId ID del administrador
     * @param comments Comentarios obligatorios explicando el rechazo
     * @return Solicitud rechazada
     */
    ApplicationRequest rejectApplication(Long id, Long adminId, String comments);

    /**
     * Elimina una solicitud (solo si está rechazada o es muy antigua)
     *
     * @param id ID de la solicitud
     */
    void deleteApplication(Long id);

    /**
     * Busca solicitudes por nombre del solicitante
     *
     * @param name Nombre o parte del nombre
     * @return Lista de solicitudes que coinciden
     */
    List<ApplicationRequest> searchApplicationsByName(String name);

    /**
     * Obtiene estadísticas generales de solicitudes
     *
     * @return Map con estadísticas (total, pendientes, aprobadas, rechazadas, por tipo, etc.)
     */
    Map<String, Object> getApplicationStatistics();

    /**
     * Obtiene las solicitudes recientes (últimos 7 días) que están pendientes
     *
     * @return Lista de solicitudes recientes pendientes
     */
    List<ApplicationRequest> getRecentPendingApplications();
}