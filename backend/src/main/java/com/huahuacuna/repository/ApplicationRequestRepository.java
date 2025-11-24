package com.huahuacuna.repository;

import com.huahuacuna.model.ApplicationRequest;
import com.huahuacuna.model.ApplicationStatus;
import com.huahuacuna.model.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la gestión de solicitudes de participación.
 * Proporciona métodos de consulta personalizados y automáticos.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Repository
public interface ApplicationRequestRepository extends JpaRepository<ApplicationRequest, Long> {

    /**
     * Busca todas las solicitudes por tipo (VOLUNTARIO o PADRINO)
     *
     * @param type Tipo de solicitud
     * @return Lista de solicitudes del tipo especificado
     */
    List<ApplicationRequest> findByType(ApplicationType type);

    /**
     * Busca todas las solicitudes por estado
     *
     * @param status Estado de la solicitud
     * @return Lista de solicitudes con el estado especificado
     */
    List<ApplicationRequest> findByStatus(ApplicationStatus status);

    /**
     * Busca solicitudes por tipo y estado
     *
     * @param type Tipo de solicitud
     * @param status Estado de la solicitud
     * @return Lista de solicitudes que coinciden con ambos criterios
     */
    List<ApplicationRequest> findByTypeAndStatus(ApplicationType type, ApplicationStatus status);

    /**
     * Busca una solicitud por correo electrónico
     *
     * @param email Correo electrónico del solicitante
     * @return Optional con la solicitud si existe
     */
    Optional<ApplicationRequest> findByEmail(String email);

    /**
     * Verifica si existe una solicitud con el correo especificado
     *
     * @param email Correo electrónico a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca solicitudes creadas después de una fecha específica
     *
     * @param date Fecha de referencia
     * @return Lista de solicitudes creadas después de la fecha
     */
    List<ApplicationRequest> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Busca solicitudes pendientes ordenadas por fecha de creación (más recientes primero)
     *
     * @return Lista de solicitudes pendientes ordenadas
     */
    @Query("SELECT a FROM ApplicationRequest a WHERE a.status = 'PENDIENTE' ORDER BY a.createdAt DESC")
    List<ApplicationRequest> findPendingApplicationsOrderByDateDesc();

    /**
     * Cuenta las solicitudes por estado
     *
     * @param status Estado a contar
     * @return Número de solicitudes con ese estado
     */
    long countByStatus(ApplicationStatus status);

    /**
     * Cuenta las solicitudes por tipo
     *
     * @param type Tipo a contar
     * @return Número de solicitudes de ese tipo
     */
    long countByType(ApplicationType type);

    /**
     * Busca las últimas N solicitudes de un tipo específico
     *
     * @param type Tipo de solicitud
     * @return Lista de las últimas solicitudes del tipo especificado
     */
    @Query("SELECT a FROM ApplicationRequest a WHERE a.type = :type ORDER BY a.createdAt DESC")
    List<ApplicationRequest> findLatestByType(@Param("type") ApplicationType type);

    /**
     * Obtiene estadísticas de solicitudes agrupadas por estado
     *
     * @return Lista de arrays con [estado, cantidad]
     */
    @Query("SELECT a.status, COUNT(a) FROM ApplicationRequest a GROUP BY a.status")
    List<Object[]> countApplicationsByStatus();

    /**
     * Obtiene solicitudes revisadas por un administrador específico
     *
     * @param adminId ID del administrador
     * @return Lista de solicitudes revisadas por el administrador
     */
    List<ApplicationRequest> findByReviewedBy(Long adminId);

    /**
     * Busca solicitudes por nombre (búsqueda parcial, case-insensitive)
     *
     * @param name Nombre o parte del nombre a buscar
     * @return Lista de solicitudes que coinciden
     */
    @Query("SELECT a FROM ApplicationRequest a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ApplicationRequest> searchByName(@Param("name") String name);

    /**
     * Obtiene las solicitudes más recientes (últimos 7 días) que están pendientes
     *
     * @param date Fecha límite (normalmente ahora - 7 días)
     * @return Lista de solicitudes recientes pendientes
     */
    @Query("SELECT a FROM ApplicationRequest a WHERE a.status = 'PENDIENTE' AND a.createdAt >= :date ORDER BY a.createdAt DESC")
    List<ApplicationRequest> findRecentPendingApplications(@Param("date") LocalDateTime date);
}