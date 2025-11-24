package com.huahuacuna.controller;

import com.huahuacuna.model.ApplicationRequest;
import com.huahuacuna.model.ApplicationStatus;
import com.huahuacuna.model.ApplicationType;
import com.huahuacuna.model.User;
import com.huahuacuna.model.dto.ApplicationRequestDTO;
import com.huahuacuna.model.dto.ApplicationResponseDTO;
import com.huahuacuna.model.dto.UpdateApplicationStatusDTO;
import com.huahuacuna.service.ApplicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de solicitudes de participación.
 *
 * Endpoints públicos: Creación de solicitudes (voluntarios y padrinos)
 * Endpoints protegidos: Gestión administrativa (solo ADMIN)
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:3000")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // ========== ENDPOINTS PÚBLICOS ==========

    /**
     * Crea una nueva solicitud de voluntariado.
     * Endpoint público accesible desde el formulario web.
     *
     * @param dto Datos de la solicitud de voluntariado
     * @return ResponseEntity con la solicitud creada y código 201
     */
    @PostMapping("/volunteer")
    public ResponseEntity<Map<String, Object>> createVolunteerApplication(
            @Valid @RequestBody ApplicationRequestDTO dto) {

        logger.info("POST /api/applications/volunteer - Recibida solicitud de voluntariado");

        try {
            // Convertir DTO a entidad
            ApplicationRequest application = new ApplicationRequest(
                    dto.getFullName(),
                    dto.getEmail(),
                    dto.getPhone(),
                    dto.getInterestArea(),
                    dto.getAvailability(),
                    dto.getPreviousExperience(),
                    dto.getAcceptsInformation()
            );

            // Crear la solicitud
            ApplicationRequest savedApplication = applicationService.createVolunteerApplication(application);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud de voluntariado recibida exitosamente");
            response.put("data", new ApplicationResponseDTO(savedApplication));

            logger.info("Solicitud de voluntariado creada exitosamente con ID: {}", savedApplication.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al crear solicitud de voluntariado", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al procesar la solicitud. Por favor, intente nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Crea una nueva solicitud de apadrinamiento.
     * Endpoint público accesible desde el formulario web.
     *
     * @param dto Datos de la solicitud de apadrinamiento
     * @return ResponseEntity con la solicitud creada y código 201
     */
    @PostMapping("/sponsor")
    public ResponseEntity<Map<String, Object>> createSponsorApplication(
            @Valid @RequestBody ApplicationRequestDTO dto) {

        logger.info("POST /api/applications/sponsor - Recibida solicitud de apadrinamiento");

        try {
            // Convertir DTO a entidad
            ApplicationRequest application = new ApplicationRequest(
                    dto.getFullName(),
                    dto.getEmail(),
                    dto.getPhone(),
                    dto.getCountry(),
                    dto.getIdNumber(),
                    dto.getIdDocumentPath()
            );

            // Crear la solicitud
            ApplicationRequest savedApplication = applicationService.createSponsorApplication(application);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud de apadrinamiento recibida exitosamente");
            response.put("data", new ApplicationResponseDTO(savedApplication));

            logger.info("Solicitud de apadrinamiento creada exitosamente con ID: {}", savedApplication.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al crear solicitud de apadrinamiento", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al procesar la solicitud. Por favor, intente nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ========== ENDPOINTS PROTEGIDOS (SOLO ADMIN) ==========

    /**
     * Obtiene todas las solicitudes del sistema.
     * Requiere autenticación y rol ADMIN.
     *
     * @return Lista de todas las solicitudes
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllApplications() {
        logger.info("GET /api/applications - Obteniendo todas las solicitudes");

        try {
            List<ApplicationRequest> applications = applicationService.getAllApplications();
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener solicitudes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las solicitudes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene una solicitud específica por su ID.
     * Requiere autenticación y rol ADMIN.
     *
     * @param id ID de la solicitud
     * @return Solicitud encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationById(@PathVariable Long id) {
        logger.info("GET /api/applications/{} - Obteniendo solicitud", id);

        try {
            ApplicationRequest application = applicationService.getApplicationById(id);
            ApplicationResponseDTO responseDTO = new ApplicationResponseDTO(application);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Solicitud no encontrada: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Solicitud no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Obtiene solicitudes filtradas por tipo.
     * Requiere autenticación y rol ADMIN.
     *
     * @param type Tipo de solicitud (VOLUNTARIO o PADRINO)
     * @return Lista de solicitudes del tipo especificado
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationsByType(@PathVariable ApplicationType type) {
        logger.info("GET /api/applications/type/{} - Filtrando por tipo", type);

        try {
            List<ApplicationRequest> applications = applicationService.getApplicationsByType(type);
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al filtrar solicitudes por tipo", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al filtrar las solicitudes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene solicitudes filtradas por estado.
     * Requiere autenticación y rol ADMIN.
     *
     * @param status Estado de la solicitud
     * @return Lista de solicitudes con ese estado
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        logger.info("GET /api/applications/status/{} - Filtrando por estado", status);

        try {
            List<ApplicationRequest> applications = applicationService.getApplicationsByStatus(status);
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al filtrar solicitudes por estado", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al filtrar las solicitudes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene todas las solicitudes pendientes.
     * Requiere autenticación y rol ADMIN.
     *
     * @return Lista de solicitudes pendientes ordenadas por fecha
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingApplications() {
        logger.info("GET /api/applications/pending - Obteniendo solicitudes pendientes");

        try {
            List<ApplicationRequest> applications = applicationService.getPendingApplications();
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener solicitudes pendientes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las solicitudes pendientes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene solicitudes recientes pendientes (últimos 7 días).
     * Requiere autenticación y rol ADMIN.
     *
     * @return Lista de solicitudes recientes pendientes
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRecentPendingApplications() {
        logger.info("GET /api/applications/recent - Obteniendo solicitudes recientes");

        try {
            List<ApplicationRequest> applications = applicationService.getRecentPendingApplications();
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener solicitudes recientes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las solicitudes recientes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Busca solicitudes por nombre del solicitante.
     * Requiere autenticación y rol ADMIN.
     *
     * @param name Nombre o parte del nombre a buscar
     * @return Lista de solicitudes que coinciden
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchApplications(@RequestParam String name) {
        logger.info("GET /api/applications/search?name={}", name);

        try {
            List<ApplicationRequest> applications = applicationService.searchApplicationsByName(name);
            List<ApplicationResponseDTO> responseDTOs = applications.stream()
                    .map(ApplicationResponseDTO::new)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("total", responseDTOs.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al buscar solicitudes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al buscar las solicitudes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtiene estadísticas generales de las solicitudes.
     * Requiere autenticación y rol ADMIN.
     *
     * @return Map con estadísticas del sistema
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("GET /api/applications/statistics - Obteniendo estadísticas");

        try {
            Map<String, Object> statistics = applicationService.getApplicationStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las estadísticas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualiza el estado de una solicitud.
     * Requiere autenticación y rol ADMIN.
     *
     * @param id             ID de la solicitud
     * @param dto            DTO con nuevo estado y comentarios
     * @param authentication Objeto de autenticación de Spring Security
     * @return Solicitud actualizada
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusDTO dto,
            Authentication authentication) {

        logger.info("PATCH /api/applications/{}/status - Actualizando estado", id);
        try {
            // Obtener el ID del administrador autenticado
            User adminUser = (User) authentication.getPrincipal();
            Long adminId = adminUser.getId();

            // Actualizar el estado
            ApplicationRequest updatedApplication = applicationService.updateApplicationStatus(
                    id,
                    dto.getStatus(),
                    adminId,
                    dto.getComments()
            );

            ApplicationResponseDTO responseDTO = new ApplicationResponseDTO(updatedApplication);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("data", responseDTO);

            logger.info("Estado de solicitud {} actualizado a {}", id, dto.getStatus());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Error al actualizar estado: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al actualizar el estado de la solicitud", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el estado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Aprueba una solicitud.
     * Requiere autenticación y rol ADMIN.
     *
     * @param id             ID de la solicitud
     * @param dto            DTO con comentarios opcionales
     * @param authentication Objeto de autenticación
     * @return Solicitud aprobada
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approveApplication(
            @PathVariable Long id,
            @RequestBody(required = false) UpdateApplicationStatusDTO dto,
            Authentication authentication) {

        logger.info("POST /api/applications/{}/approve - Aprobando solicitud", id);

        try {
            User adminUser = (User) authentication.getPrincipal();
            Long adminId = adminUser.getId();

            String comments = (dto != null) ? dto.getComments() : null;
            ApplicationRequest approvedApplication = applicationService.approveApplication(id, adminId, comments);

            ApplicationResponseDTO responseDTO = new ApplicationResponseDTO(approvedApplication);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud aprobada exitosamente");
            response.put("data", responseDTO);

            logger.info("Solicitud {} aprobada por administrador {}", id, adminId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Error al aprobar solicitud: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al aprobar la solicitud", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al aprobar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Rechaza una solicitud.
     * Requiere autenticación y rol ADMIN.
     *
     * @param id             ID de la solicitud
     * @param dto            DTO con comentarios obligatorios
     * @param authentication Objeto de autenticación
     * @return Solicitud rechazada
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusDTO dto,
            Authentication authentication) {

        logger.info("POST /api/applications/{}/reject - Rechazando solicitud", id);

        try {
            User adminUser = (User) authentication.getPrincipal();
            Long adminId = adminUser.getId();

            ApplicationRequest rejectedApplication = applicationService.rejectApplication(
                    id,
                    adminId,
                    dto.getComments()
            );

            ApplicationResponseDTO responseDTO = new ApplicationResponseDTO(rejectedApplication);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud rechazada");
            response.put("data", responseDTO);

            logger.info("Solicitud {} rechazada por administrador {}", id, adminId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al rechazar: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al rechazar la solicitud", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al rechazar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Elimina una solicitud.
     * Solo se pueden eliminar solicitudes rechazadas.
     * Requiere autenticación y rol ADMIN.
     *
     * @param id ID de la solicitud a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteApplication(@PathVariable Long id) {
        logger.info("DELETE /api/applications/{} - Eliminando solicitud", id);

        try {
            applicationService.deleteApplication(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud eliminada exitosamente");

            logger.info("Solicitud {} eliminada", id);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            logger.warn("No se puede eliminar la solicitud: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            logger.warn("Solicitud no encontrada: {}", id);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Solicitud no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al eliminar la solicitud", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}