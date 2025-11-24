package com.huahuacuna.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de correos electrónicos.
 * Utiliza JavaMailSender de Spring Boot para enviar emails HTML.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.fromName}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // ========== MÉTODOS EXISTENTES ==========

    /**
     * Envía un email de recuperación de contraseña
     *
     * @param toEmail Email del destinatario
     * @param userName Nombre del usuario
     * @param resetToken Token de recuperación
     */
    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Recuperación de contraseña - Fundación Huahuacuna");

            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailHtml(userName, resetUrl, resetToken);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de recuperación enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar email de recuperación a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Error al enviar el email de recuperación", e);
        }
    }

    /**
     * Construye el contenido HTML del email de recuperación
     */
    private String buildPasswordResetEmailHtml(String userName, String resetUrl, String token) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperación de Contraseña</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 20px; text-align: center;">
                                        <h1 style="color: #FDD835; margin: 0; font-size: 28px; font-weight: bold;">
                                            🔐 Recuperación de Contraseña
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Body -->
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Hola <strong>%s</strong>,
                                        </p>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Recibimos una solicitud para restablecer tu contraseña en la plataforma de 
                                            <strong>Fundación Huahuacuna</strong>.
                                        </p>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 0 0 30px 0;">
                                            Haz clic en el siguiente botón para crear una nueva contraseña:
                                        </p>
                                        
                                        <div style="text-align: center; margin: 30px 0;">
                                            <a href="%s" 
                                               style="display: inline-block; background-color: #FDD835; color: #1E3A5F; 
                                                      padding: 14px 40px; text-decoration: none; border-radius: 25px; 
                                                      font-weight: bold; font-size: 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                                Restablecer Contraseña
                                            </a>
                                        </div>
                                        
                                        <p style="color: #777; font-size: 13px; line-height: 1.6; margin: 30px 0 0 0; padding-top: 20px; border-top: 1px solid #eee;">
                                            O copia y pega este enlace en tu navegador:
                                        </p>
                                        <p style="color: #1E3A5F; font-size: 12px; word-break: break-all; margin: 10px 0;">
                                            %s
                                        </p>
                                        
                                        <div style="background-color: #FFF9E6; border-left: 4px solid #FDD835; padding: 15px; margin: 30px 0;">
                                            <p style="color: #856404; font-size: 13px; margin: 0; line-height: 1.5;">
                                                <strong>⚠️ Importante:</strong> Este enlace expirará en <strong>1 hora</strong>. 
                                                Si no solicitaste este cambio, ignora este correo.
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; font-size: 12px; margin: 0 0 10px 0;">
                                            Este es un correo automático, por favor no respondas a este mensaje.
                                        </p>
                                        <p style="color: #6c757d; font-size: 12px; margin: 0;">
                                            © 2025 Fundación Huahuacuna. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, resetUrl, resetUrl);
    }

    /**
     * Envía un email de bienvenida a nuevos usuarios
     *
     * @param toEmail Email del destinatario
     * @param userName Nombre del usuario
     */
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a Fundación Huahuacuna");

            String htmlContent = buildWelcomeEmailHtml(userName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de bienvenida enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye el contenido HTML del email de bienvenida
     */
    private String buildWelcomeEmailHtml(String userName) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bienvenido</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden;">
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 20px; text-align: center;">
                                        <h1 style="color: #FDD835; margin: 0; font-size: 28px;">
                                            ¡Bienvenido a Fundación Huahuacuna! 🎉
                                        </h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <p style="color: #333; font-size: 16px; line-height: 1.6;">
                                            Hola <strong>%s</strong>,
                                        </p>
                                        <p style="color: #555; font-size: 14px; line-height: 1.6;">
                                            ¡Gracias por unirte a nuestra comunidad! Estamos emocionados de tenerte con nosotros.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                                        <p style="color: #6c757d; font-size: 12px; margin: 0;">
                                            © 2025 Fundación Huahuacuna
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName);
    }

    // ========== NUEVOS MÉTODOS PARA SOLICITUDES ==========

    /**
     * Envía un email genérico (usado para notificaciones de solicitudes)
     *
     * @param toEmail Email del destinatario
     * @param subject Asunto del email
     * @param htmlContent Contenido HTML del email
     */
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    /**
     * Envía notificación al administrador sobre nueva solicitud
     *
     * @param adminEmail Email del administrador
     * @param applicantName Nombre del solicitante
     * @param applicationType Tipo de solicitud (Voluntario/Padrino)
     * @param applicantEmail Email del solicitante
     * @param applicantPhone Teléfono del solicitante
     */
    public void sendNewApplicationNotification(
            String adminEmail,
            String applicantName,
            String applicationType,
            String applicantEmail,
            String applicantPhone) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmail);
            helper.setSubject("Nueva solicitud de " + applicationType + " - Fundación Huahuacuna");

            String htmlContent = buildNewApplicationNotificationHtml(
                    applicantName, applicationType, applicantEmail, applicantPhone);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de notificación de solicitud enviado a administrador: {}", adminEmail);

        } catch (Exception e) {
            log.error("Error al enviar notificación de solicitud al administrador {}: {}", adminEmail, e.getMessage());
            // No lanzar excepción para no interrumpir el flujo
        }
    }

    /**
     * Construye el HTML para notificación de nueva solicitud al administrador
     */
    private String buildNewApplicationNotificationHtml(
            String applicantName,
            String applicationType,
            String applicantEmail,
            String applicantPhone) {

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nueva Solicitud</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 30px 20px; text-align: center;">
                                        <h1 style="color: #FDD835; margin: 0; font-size: 24px; font-weight: bold;">
                                            📋 Nueva Solicitud Recibida
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Body -->
                                <tr>
                                    <td style="padding: 30px;">
                                        <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Se ha recibido una nueva solicitud de <strong>%s</strong>.
                                        </p>
                                        
                                        <div style="background-color: #f8f9fa; border-radius: 8px; padding: 20px; margin: 20px 0;">
                                            <h3 style="color: #1E3A5F; margin: 0 0 15px 0; font-size: 16px;">
                                                Detalles del Solicitante:
                                            </h3>
                                            <table width="100%%" cellpadding="8" cellspacing="0">
                                                <tr>
                                                    <td style="color: #6c757d; font-size: 13px; font-weight: bold; width: 30%%;">Nombre:</td>
                                                    <td style="color: #333; font-size: 14px;">%s</td>
                                                </tr>
                                                <tr>
                                                    <td style="color: #6c757d; font-size: 13px; font-weight: bold;">Email:</td>
                                                    <td style="color: #333; font-size: 14px;">%s</td>
                                                </tr>
                                                <tr>
                                                    <td style="color: #6c757d; font-size: 13px; font-weight: bold;">Teléfono:</td>
                                                    <td style="color: #333; font-size: 14px;">%s</td>
                                                </tr>
                                            </table>
                                        </div>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 20px 0;">
                                            Por favor, revisa la solicitud en el panel administrativo para aprobarla o rechazarla.
                                        </p>
                                        
                                        <div style="text-align: center; margin: 30px 0;">
                                            <a href="%s/dashboard" 
                                               style="display: inline-block; background-color: #FDD835; color: #1E3A5F; 
                                                      padding: 12px 30px; text-decoration: none; border-radius: 25px; 
                                                      font-weight: bold; font-size: 14px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                                Ir al Panel Administrativo
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; font-size: 12px; margin: 0;">
                                            © 2025 Fundación Huahuacuna. Sistema de Gestión.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(applicationType.toLowerCase(), applicantName, applicantEmail, applicantPhone, frontendUrl);
    }

    /**
     * Envía email de confirmación al solicitante
     *
     * @param toEmail Email del solicitante
     * @param applicantName Nombre del solicitante
     * @param applicationType Tipo de solicitud
     */
    public void sendApplicationConfirmation(String toEmail, String applicantName, String applicationType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de solicitud - Fundación Huahuacuna");

            String htmlContent = buildApplicationConfirmationHtml(applicantName, applicationType);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación de solicitud enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar confirmación de solicitud a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye el HTML para confirmación de solicitud al solicitante
     */
    private String buildApplicationConfirmationHtml(String applicantName, String applicationType) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirmación de Solicitud</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 30px 20px; text-align: center;">
                                        <h1 style="color: #FDD835; margin: 0; font-size: 24px; font-weight: bold;">
                                            ✅ Solicitud Recibida
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Body -->
                                <tr>
                                    <td style="padding: 30px;">
                                        <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 15px 0;">
                                            Estimado/a <strong>%s</strong>,
                                        </p>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 0 0 15px 0;">
                                            Hemos recibido tu solicitud para <strong>%s</strong> en la Fundación Huahuacuna.
                                        </p>
                                        
                                        <div style="background-color: #E8F5E9; border-left: 4px solid #4CAF50; padding: 15px; margin: 20px 0; border-radius: 4px;">
                                            <p style="color: #2E7D32; font-size: 13px; margin: 0; line-height: 1.5;">
                                                <strong>✓ Tu solicitud está siendo revisada</strong><br>
                                                Nuestro equipo la evaluará pronto y te contactaremos.
                                            </p>
                                        </div>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 20px 0;">
                                            Gracias por tu interés en colaborar con nuestra fundación. 
                                            Tu compromiso es fundamental para transformar vidas.
                                        </p>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 0;">
                                            Atentamente,<br>
                                            <strong>Equipo Fundación Huahuacuna</strong>
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; font-size: 12px; margin: 0 0 5px 0;">
                                            Este es un correo automático, por favor no respondas a este mensaje.
                                        </p>
                                        <p style="color: #6c757d; font-size: 12px; margin: 0;">
                                            © 2025 Fundación Huahuacuna. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(applicantName, applicationType.equals("VOLUNTARIO") ? "ser voluntario/a" : "apadrinar un niño");
    }

    /**
     * Envía email de actualización de estado de solicitud
     *
     * @param toEmail Email del solicitante
     * @param applicantName Nombre del solicitante
     * @param status Estado de la solicitud (APROBADO/RECHAZADO)
     * @param comments Comentarios del administrador
     */
    public void sendApplicationStatusUpdate(
            String toEmail,
            String applicantName,
            String status,
            String comments) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Actualización de tu solicitud - Fundación Huahuacuna");

            String htmlContent = buildApplicationStatusUpdateHtml(applicantName, status, comments);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de actualización de estado enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar actualización de estado a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye el HTML para actualización de estado de solicitud
     */
    private String buildApplicationStatusUpdateHtml(String applicantName, String status, String comments) {
        boolean isApproved = "APROBADO".equals(status);
        String statusColor = isApproved ? "#4CAF50" : "#F44336";
        String statusBgColor = isApproved ? "#E8F5E9" : "#FFEBEE";
        String statusIcon = isApproved ? "✓" : "✗";
        String statusText = isApproved ? "Aprobada" : "No Aprobada";
        String statusMessage = isApproved
                ? "¡Felicitaciones! Tu solicitud ha sido aprobada. Pronto nos pondremos en contacto contigo para los siguientes pasos."
                : "Lamentablemente, tu solicitud no ha sido aprobada en este momento.";

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Actualización de Solicitud</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 30px 20px; text-align: center;">
                                        <h1 style="color: #FDD835; margin: 0; font-size: 24px; font-weight: bold;">
                                            Actualización de Solicitud
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Body -->
                                <tr>
                                    <td style="padding: 30px;">
                                        <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 15px 0;">
                                            Estimado/a <strong>%s</strong>,
                                        </p>
                                        
                                        <div style="background-color: %s; border-left: 4px solid %s; padding: 15px; margin: 20px 0; border-radius: 4px;">
                                            <p style="color: %s; font-size: 15px; margin: 0; line-height: 1.5; font-weight: bold;">
                                                %s Solicitud %s
                                            </p>
                                        </div>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 20px 0;">
                                            %s
                                        </p>
                                        
                                        %s
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 20px 0 0 0;">
                                            Si tienes alguna pregunta, no dudes en contactarnos.
                                        </p>
                                        
                                        <p style="color: #555; font-size: 14px; line-height: 1.6; margin: 15px 0 0 0;">
                                            Atentamente,<br>
                                            <strong>Equipo Fundación Huahuacuna</strong>
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; font-size: 12px; margin: 0;">
                                            © 2025 Fundación Huahuacuna. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                applicantName,
                statusBgColor,
                statusColor,
                statusColor,
                statusIcon,
                statusText,
                statusMessage,
                (comments != null && !comments.isEmpty())

                        ? "<div style=\"background-color: #f8f9fa; border-radius: 8px; padding: 15px; margin: 20px 0;\">" +
                        "<p style=\"color: #6c757d; font-size: 13px; margin: 0 0 5px 0; font-weight: bold;\">Comentarios:</p>" +
                        "<p style=\"color: #333; font-size: 14px; margin: 0; line-height: 1.5;\">" + comments + "</p>" +
                        "</div>"
                        : ""
        );
    }
    // ========== AGREGAR ESTOS MÉTODOS AL FINAL DE TU EmailService.java ==========

    /**
     * Envía email de confirmación de donación monetaria
     *
     * @param toEmail Email del donante
     * @param donorName Nombre del donante
     * @param donationId ID de la donación
     * @param amount Monto donado
     * @param paymentMethod Método de pago
     */
    public void sendMonetaryDonationConfirmation(
            String toEmail,
            String donorName,
            Long donationId,
            Double amount,
            String paymentMethod) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ Confirmación de Donación - Fundación Huahuacuna");

            String htmlContent = buildMonetaryDonationConfirmationHtml(
                    donorName, donationId, amount, paymentMethod);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación de donación monetaria enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar confirmación de donación a {}: {}", toEmail, e.getMessage());
            // No lanzar excepción para no interrumpir el proceso
        }
    }

    /**
     * Construye el HTML para confirmación de donación monetaria
     */
    private String buildMonetaryDonationConfirmationHtml(
            String donorName,
            Long donationId,
            Double amount,
            String paymentMethod) {

        String formattedAmount = String.format("%,.0f", amount);
        String paymentMethodText = "ONLINE".equals(paymentMethod) ? "Pago en línea" : "Transferencia bancaria";

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirmación de Donación</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #f5f5f5;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 30px; text-align: center;">
                                        <div style="width: 80px; height: 80px; background-color: #FDD835; border-radius: 50%%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;">
                                            <span style="font-size: 40px;">✓</span>
                                        </div>
                                        <h1 style="color: #FDD835; margin: 0; font-size: 28px; font-weight: bold;">
                                            ¡Gracias por tu Donación!
                                        </h1>
                                        <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">
                                            Fundación Huahuacuna
                                        </p>
                                    </td>
                                </tr>

                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Estimado/a <strong>%s</strong>,
                                        </p>
                                        
                                        <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;">
                                            Hemos recibido tu donación exitosamente. Tu generosidad nos permite continuar transformando vidas y apoyando a las familias de nuestra comunidad.
                                        </p>

                                        <!-- Donation Details Card -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 8px; margin-bottom: 30px;">
                                            <tr>
                                                <td style="padding: 25px;">
                                                    <h2 style="color: #1E3A5F; font-size: 18px; margin: 0 0 15px 0;">
                                                        📋 Detalles de tu Donación
                                                    </h2>
                                                    
                                                    <table width="100%%" cellpadding="8" cellspacing="0">
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>ID de Donación:</strong>
                                                            </td>
                                                            <td align="right" style="color: #333333; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                #%d
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>Monto:</strong>
                                                            </td>
                                                            <td align="right" style="color: #28a745; font-size: 18px; font-weight: bold; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                $%s COP
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>Método de Pago:</strong>
                                                            </td>
                                                            <td align="right" style="color: #333333; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                %s
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0;">
                                                                <strong>Fecha:</strong>
                                                            </td>
                                                            <td align="right" style="color: #333333; font-size: 14px; padding: 8px 0;">
                                                                %s
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Impact Message -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background: linear-gradient(135deg, #FDD835 0%%, #FDB835 100%%); border-radius: 8px; margin-bottom: 30px;">
                                            <tr>
                                                <td style="padding: 20px; text-align: center;">
                                                    <p style="color: #1E3A5F; font-size: 16px; font-weight: bold; margin: 0;">
                                                        💛 Tu apoyo marca la diferencia
                                                    </p>
                                                    <p style="color: #1E3A5F; font-size: 14px; margin: 10px 0 0 0;">
                                                        Gracias a personas como tú, podemos continuar con nuestra misión
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>

                                        <p style="color: #666666; font-size: 14px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Este correo sirve como comprobante de tu donación. Si tienes alguna pregunta, no dudes en contactarnos.
                                        </p>

                                        <!-- CTA Button -->
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="center" style="padding: 20px 0;">
                                                    <a href="%s" style="display: inline-block; padding: 15px 40px; background-color: #1E3A5F; color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px;">
                                                        Conoce Nuestro Trabajo
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 30px; text-align: center; border-top: 1px solid #e0e0e0;">
                                        <p style="color: #666666; font-size: 14px; margin: 0 0 10px 0;">
                                            <strong>Fundación Huahuacuna</strong>
                                        </p>
                                        <p style="color: #999999; font-size: 12px; margin: 0 0 10px 0;">
                                            Transformando vidas, construyendo futuro
                                        </p>
                                        <p style="color: #999999; font-size: 12px; margin: 0;">
                                            📧 contacto@huahuacuna.org | 📱 +57 300 123 4567
                                        </p>
                                        <p style="color: #999999; font-size: 11px; margin: 15px 0 0 0;">
                                            © 2025 Fundación Huahuacuna. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                donorName,
                donationId,
                formattedAmount,
                paymentMethodText,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                frontendUrl
        );
    }

    /**
     * Envía email de confirmación de donación en especie
     *
     * @param toEmail Email del donante
     * @param donorName Nombre del donante
     * @param donationId ID de la donación
     * @param itemType Tipo de artículo
     * @param description Descripción
     */
    public void sendInKindDonationConfirmation(
            String toEmail,
            String donorName,
            Long donationId,
            String itemType,
            String description) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ Confirmación de Donación en Especie - Fundación Huahuacuna");

            String htmlContent = buildInKindDonationConfirmationHtml(
                    donorName, donationId, itemType, description);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación de donación en especie enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar confirmación de donación en especie a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye el HTML para confirmación de donación en especie
     */
    private String buildInKindDonationConfirmationHtml(
            String donorName,
            Long donationId,
            String itemType,
            String description) {

        String itemTypeText = switch (itemType.toLowerCase()) {
            case "ropa" -> "Ropa y calzado";
            case "alimentos" -> "Alimentos no perecederos";
            case "juguetes" -> "Juguetes";
            case "libros" -> "Libros y útiles escolares";
            case "electrodomesticos" -> "Electrodomésticos";
            case "muebles" -> "Muebles";
            default -> "Otros artículos";
        };

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirmación de Donación en Especie</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #f5f5f5;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 30px; text-align: center;">
                                        <div style="width: 80px; height: 80px; background-color: #FDD835; border-radius: 50%%; margin: 0 auto 20px;">
                                            <span style="font-size: 40px; line-height: 80px;">✓</span>
                                        </div>
                                        <h1 style="color: #FDD835; margin: 0; font-size: 28px; font-weight: bold;">
                                            ¡Gracias por tu Donación!
                                        </h1>
                                        <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">
                                            Fundación Huahuacuna
                                        </p>
                                    </td>
                                </tr>

                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                            Estimado/a <strong>%s</strong>,
                                        </p>
                                        
                                        <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;">
                                            Hemos recibido tu solicitud de donación en especie. Tu generosidad nos permite continuar apoyando a las familias de nuestra comunidad.
                                        </p>

                                        <!-- Donation Details Card -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 8px; margin-bottom: 30px;">
                                            <tr>
                                                <td style="padding: 25px;">
                                                    <h2 style="color: #1E3A5F; font-size: 18px; margin: 0 0 15px 0;">
                                                        📋 Detalles de tu Donación
                                                    </h2>
                                                    
                                                    <table width="100%%" cellpadding="8" cellspacing="0">
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>ID de Solicitud:</strong>
                                                            </td>
                                                            <td align="right" style="color: #333333; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                #%d
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>Tipo de Artículo:</strong>
                                                            </td>
                                                            <td align="right" style="color: #7B1FA2; font-size: 16px; font-weight: bold; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                %s
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2" style="color: #666666; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #e0e0e0;">
                                                                <strong>Descripción:</strong>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2" style="color: #333333; font-size: 14px; padding: 8px 0; line-height: 1.5;">
                                                                %s
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Next Steps -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #E3F2FD; border-radius: 8px; margin-bottom: 30px;">
                                            <tr>
                                                <td style="padding: 20px;">
                                                    <h3 style="color: #1565C0; font-size: 16px; margin: 0 0 10px 0;">
                                                        📞 Próximos Pasos
                                                    </h3>
                                                    <p style="color: #333333; font-size: 14px; margin: 0; line-height: 1.6;">
                                                        Nuestro equipo se pondrá en contacto contigo en las próximas <strong>24-48 horas</strong> 
                                                        para coordinar la recolección o entrega de los artículos.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Impact Message -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background: linear-gradient(135deg, #FDD835 0%%, #FDB835 100%%); border-radius: 8px; margin-bottom: 30px;">
                                            <tr>
                                                <td style="padding: 20px; text-align: center;">
                                                    <p style="color: #1E3A5F; font-size: 16px; font-weight: bold; margin: 0;">
                                                        💛 Tu donación impacta vidas
                                                    </p>
                                                    <p style="color: #1E3A5F; font-size: 14px; margin: 10px 0 0 0;">
                                                        Gracias por tu solidaridad y compromiso con nuestra comunidad
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>

                                        <p style="color: #666666; font-size: 14px; line-height: 1.6; margin: 0;">
                                            Si tienes alguna pregunta, no dudes en contactarnos respondiendo a este correo.
                                        </p>
                                    </td>
                                </tr>

                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 30px; text-align: center; border-top: 1px solid #e0e0e0;">
                                        <p style="color: #666666; font-size: 14px; margin: 0 0 10px 0;">
                                            <strong>Fundación Huahuacuna</strong>
                                        </p>
                                        <p style="color: #999999; font-size: 12px; margin: 0 0 10px 0;">
                                            Transformando vidas, construyendo futuro
                                        </p>
                                        <p style="color: #999999; font-size: 12px; margin: 0;">
                                            📧 contacto@huahuacuna.org | 📱 +57 300 123 4567
                                        </p>
                                        <p style="color: #999999; font-size: 11px; margin: 15px 0 0 0;">
                                            © 2025 Fundación Huahuacuna. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                donorName,
                donationId,
                itemTypeText,
                description
        );
    }
}