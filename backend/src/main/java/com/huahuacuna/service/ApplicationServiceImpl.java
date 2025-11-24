package com.huahuacuna.service;

import com.huahuacuna.model.ApplicationRequest;
import com.huahuacuna.model.ApplicationStatus;
import com.huahuacuna.model.ApplicationType;
import com.huahuacuna.repository.ApplicationRequestRepository;
import com.huahuacuna.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de gestión de solicitudes de participación.
 * Contiene toda la lógica de negocio relacionada con las solicitudes.
 *
 * @author Fundación Huahuacuna
 * @version 1.0
 */
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRequestRepository applicationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationServiceImpl(
            ApplicationRequestRepository applicationRepository,
            NotificationService notificationService,
            EmailService emailService,
            UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationRequest createVolunteerApplication(ApplicationRequest applicationRequest) {
        logger.info("Creando solicitud de voluntariado para: {}", applicationRequest.getEmail());

        // Validar que no exista una solicitud con el mismo email
        if (applicationRepository.existsByEmail(applicationRequest.getEmail())) {
            logger.warn("Ya existe una solicitud con el email: {}", applicationRequest.getEmail());
            throw new IllegalArgumentException("Ya existe una solicitud registrada con este correo electrónico");
        }

        // Establecer el tipo como VOLUNTARIO
        applicationRequest.setType(ApplicationType.VOLUNTARIO);
        applicationRequest.setStatus(ApplicationStatus.PENDIENTE);

        // Guardar la solicitud
        ApplicationRequest savedApplication = applicationRepository.save(applicationRequest);
        logger.info("Solicitud de voluntariado creada con ID: {}", savedApplication.getId());

        // Crear notificación para administradores
        notifyAdminsAboutNewApplication(savedApplication);

        // Enviar correo al administrador
        sendAdminNotificationEmail(savedApplication);

        // Enviar correo de confirmación al solicitante
        sendApplicantConfirmationEmail(savedApplication);

        return savedApplication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationRequest createSponsorApplication(ApplicationRequest applicationRequest) {
        logger.info("Creando solicitud de apadrinamiento para: {}", applicationRequest.getEmail());

        // Validar que no exista una solicitud con el mismo email
        if (applicationRepository.existsByEmail(applicationRequest.getEmail())) {
            logger.warn("Ya existe una solicitud con el email: {}", applicationRequest.getEmail());
            throw new IllegalArgumentException("Ya existe una solicitud registrada con este correo electrónico");
        }

        // Establecer el tipo como PADRINO
        applicationRequest.setType(ApplicationType.PADRINO);
        applicationRequest.setStatus(ApplicationStatus.PENDIENTE);

        // Guardar la solicitud
        ApplicationRequest savedApplication = applicationRepository.save(applicationRequest);
        logger.info("Solicitud de apadrinamiento creada con ID: {}", savedApplication.getId());

        // Crear notificación para administradores
        notifyAdminsAboutNewApplication(savedApplication);

        // Enviar correo al administrador
        sendAdminNotificationEmail(savedApplication);

        // Enviar correo de confirmación al solicitante
        sendApplicantConfirmationEmail(savedApplication);

        return savedApplication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ApplicationRequest getApplicationById(Long id) {
        logger.info("Buscando solicitud con ID: {}", id);
        return applicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Solicitud no encontrada con ID: {}", id);
                    return new RuntimeException("Solicitud no encontrada con ID: " + id);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> getAllApplications() {
        logger.info("Obteniendo todas las solicitudes");
        return applicationRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> getApplicationsByType(ApplicationType type) {
        logger.info("Obteniendo solicitudes de tipo: {}", type);
        return applicationRepository.findByType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> getApplicationsByStatus(ApplicationStatus status) {
        logger.info("Obteniendo solicitudes con estado: {}", status);
        return applicationRepository.findByStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> getPendingApplications() {
        logger.info("Obteniendo solicitudes pendientes");
        return applicationRepository.findPendingApplicationsOrderByDateDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationRequest updateApplicationStatus(Long id, ApplicationStatus status, Long adminId, String comments) {
        logger.info("Actualizando estado de solicitud {} a {}", id, status);

        ApplicationRequest application = getApplicationById(id);

        // Actualizar estado y datos de revisión
        application.setStatus(status);
        application.setReviewedBy(adminId);
        application.setReviewedAt(LocalDateTime.now());

        if (comments != null && !comments.trim().isEmpty()) {
            application.setAdminComments(comments);
        }

        ApplicationRequest updatedApplication = applicationRepository.save(application);
        logger.info("Solicitud {} actualizada exitosamente", id);

        // Enviar correo al solicitante informando del cambio de estado
        sendStatusUpdateEmail(updatedApplication);

        return updatedApplication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationRequest approveApplication(Long id, Long adminId, String comments) {
        logger.info("Aprobando solicitud {}", id);
        return updateApplicationStatus(id, ApplicationStatus.APROBADO, adminId, comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationRequest rejectApplication(Long id, Long adminId, String comments) {
        logger.info("Rechazando solicitud {}", id);

        if (comments == null || comments.trim().isEmpty()) {
            throw new IllegalArgumentException("Se requieren comentarios al rechazar una solicitud");
        }

        return updateApplicationStatus(id, ApplicationStatus.RECHAZADO, adminId, comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteApplication(Long id) {
        logger.info("Eliminando solicitud {}", id);

        ApplicationRequest application = getApplicationById(id);

        // Solo permitir eliminar solicitudes rechazadas o muy antiguas
        if (application.getStatus() != ApplicationStatus.RECHAZADO) {
            throw new IllegalStateException("Solo se pueden eliminar solicitudes rechazadas");
        }

        applicationRepository.delete(application);
        logger.info("Solicitud {} eliminada exitosamente", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> searchApplicationsByName(String name) {
        logger.info("Buscando solicitudes por nombre: {}", name);
        return applicationRepository.searchByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getApplicationStatistics() {
        logger.info("Calculando estadísticas de solicitudes");

        Map<String, Object> stats = new HashMap<>();

        // Totales generales
        stats.put("total", applicationRepository.count());
        stats.put("pendientes", applicationRepository.countByStatus(ApplicationStatus.PENDIENTE));
        stats.put("enRevision", applicationRepository.countByStatus(ApplicationStatus.EN_REVISION));
        stats.put("aprobadas", applicationRepository.countByStatus(ApplicationStatus.APROBADO));
        stats.put("rechazadas", applicationRepository.countByStatus(ApplicationStatus.RECHAZADO));

        // Por tipo
        stats.put("totalVoluntarios", applicationRepository.countByType(ApplicationType.VOLUNTARIO));
        stats.put("totalPadrinos", applicationRepository.countByType(ApplicationType.PADRINO));

        // Recientes (últimos 7 días)
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        List<ApplicationRequest> recentApplications = applicationRepository.findByCreatedAtAfter(lastWeek);
        stats.put("recientes", recentApplications.size());

        logger.info("Estadísticas calculadas: {}", stats);
        return stats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRequest> getRecentPendingApplications() {
        logger.info("Obteniendo solicitudes pendientes recientes");
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        return applicationRepository.findRecentPendingApplications(lastWeek);
    }

    // ========== MÉTODOS PRIVADOS AUXILIARES ==========

    /**
     * Notifica a todos los administradores sobre una nueva solicitud
     */
    private void notifyAdminsAboutNewApplication(ApplicationRequest application) {
        try {
            // Obtener todos los usuarios con rol ADMIN
            List<Long> adminIds = userRepository.findAll().stream()
                    .filter(user -> user.getRole().toString().equals("ADMIN"))
                    .map(user -> user.getId())
                    .toList();

            String title = "Nueva solicitud de " +
                    (application.isVolunteer() ? "voluntariado" : "apadrinamiento");
            String message = String.format(
                    "Se ha recibido una nueva solicitud de %s de %s (%s)",
                    application.getType().toString().toLowerCase(),
                    application.getFullName(),
                    application.getEmail()
            );

            // Crear notificación para cada administrador
            for (Long adminId : adminIds) {
                notificationService.createNotification(
                        title,
                        message,
                        "INFO",
                        adminId,
                        application.getId()
                );
            }

            logger.info("Notificaciones creadas para {} administradores", adminIds.size());
        } catch (Exception e) {
            logger.error("Error al crear notificaciones para administradores", e);
        }
    }

    /**
     * Envía correo al administrador sobre nueva solicitud
     */
    private void sendAdminNotificationEmail(ApplicationRequest application) {
        try {
            // Email del administrador (puedes obtenerlo de configuración)
            String adminEmail = "admin@huahuacuna.org";

            String applicationType = application.isVolunteer() ? "Voluntariado" : "Apadrinamiento";

            emailService.sendNewApplicationNotification(
                    adminEmail,
                    application.getFullName(),
                    applicationType,
                    application.getEmail(),
                    application.getPhone()
            );

            logger.info("Correo de notificación enviado al administrador");
        } catch (Exception e) {
            logger.error("Error al enviar correo al administrador", e);
            // No lanzar excepción para no interrumpir el flujo principal
        }
    }

    /**
     * Envía correo de confirmación al solicitante
     */
    private void sendApplicantConfirmationEmail(ApplicationRequest application) {
        try {
            String applicationType = application.getType().toString();

            emailService.sendApplicationConfirmation(
                    application.getEmail(),
                    application.getFullName(),
                    applicationType
            );

            logger.info("Correo de confirmación enviado a: {}", application.getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar correo de confirmación al solicitante", e);
            // No lanzar excepción para no interrumpir el flujo principal
        }
    }

    /**
     * Envía correo al solicitante sobre cambio de estado
     */
    private void sendStatusUpdateEmail(ApplicationRequest application) {
        try {
            emailService.sendApplicationStatusUpdate(
                    application.getEmail(),
                    application.getFullName(),
                    application.getStatus().toString(),
                    application.getAdminComments()
            );

            logger.info("Correo de actualización enviado a: {}", application.getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar correo de actualización", e);
            // No lanzar excepción para no interrumpir el flujo principal
        }
    }

    /**
     * Construye el cuerpo del correo para el administrador
     */
    private String buildAdminEmailBody(ApplicationRequest application) {
        StringBuilder body = new StringBuilder();
        body.append("Se ha recibido una nueva solicitud de ")
                .append(application.isVolunteer() ? "voluntariado" : "apadrinamiento")
                .append(".\n\n");
        body.append("Detalles de la solicitud:\n");
        body.append("- Nombre: ").append(application.getFullName()).append("\n");
        body.append("- Email: ").append(application.getEmail()).append("\n");
        body.append("- Teléfono: ").append(application.getPhone()).append("\n");

        if (application.isVolunteer()) {
            body.append("- Área de interés: ").append(application.getInterestArea()).append("\n");
            body.append("- Disponibilidad: ").append(application.getAvailability()).append("\n");
        } else {
            body.append("- País: ").append(application.getCountry()).append("\n");
            body.append("- Cédula: ").append(application.getIdNumber()).append("\n");
        }

        body.append("\n");
        body.append("Fecha de solicitud: ").append(application.getCreatedAt()).append("\n");
        body.append("\n");
        body.append("Por favor, revisa la solicitud en el panel de administración.\n");

        return body.toString();
    }

    /**
     * Construye el cuerpo del correo de confirmación para el solicitante
     */
    private String buildApplicantConfirmationEmailBody(ApplicationRequest application) {
        return String.format(
                "Estimado/a %s,\n\n" +
                        "Hemos recibido tu solicitud para %s en la Fundación Huahuacuna.\n\n" +
                        "Tu solicitud está siendo revisada por nuestro equipo y te contactaremos pronto.\n\n" +
                        "Gracias por tu interés en colaborar con nuestra fundación.\n\n" +
                        "Atentamente,\n" +
                        "Equipo Fundación Huahuacuna",
                application.getFullName(),
                application.isVolunteer() ? "ser voluntario/a" : "apadrinar un niño"
        );
    }

    /**
     * Construye el cuerpo del correo de actualización de estado
     */
    private String buildStatusUpdateEmailBody(ApplicationRequest application) {
        String statusMessage = switch (application.getStatus()) {
            case APROBADO -> "¡Tu solicitud ha sido aprobada! Pronto nos pondremos en contacto contigo para los siguientes pasos.";
            case RECHAZADO -> "Lamentablemente, tu solicitud no ha sido aprobada en este momento.";
            case EN_REVISION -> "Tu solicitud está siendo revisada por nuestro equipo.";
            default -> "El estado de tu solicitud ha sido actualizado.";
        };

        StringBuilder body = new StringBuilder();
        body.append(String.format("Estimado/a %s,\n\n", application.getFullName()));
        body.append(statusMessage).append("\n\n");

        if (application.getAdminComments() != null && !application.getAdminComments().isEmpty()) {
            body.append("Comentarios: ").append(application.getAdminComments()).append("\n\n");
        }

        body.append("Si tienes alguna pregunta, no dudes en contactarnos.\n\n");
        body.append("Atentamente,\n");
        body.append("Equipo Fundación Huahuacuna");

        return body.toString();
    }
}