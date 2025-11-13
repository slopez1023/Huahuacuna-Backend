package com.huahuacuna.service;

import com.huahuacuna.model.Donation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.huahuacuna.model.Donation;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    // ========== MÉTODOS EXISTENTES - RECUPERACIÓN DE CONTRASEÑA ==========

    /**
     * Envía un email de recuperación de contraseña
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

    private String buildPasswordResetEmailHtml(String userName, String resetUrl, String token) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr><td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden;">
                            <tr><td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 20px; text-align: center;">
                                <h1 style="color: #FDD835; margin: 0; font-size: 28px;">🔐 Recuperación de Contraseña</h1>
                            </td></tr>
                            <tr><td style="padding: 40px 30px;">
                                <p style="color: #333; font-size: 16px;">Hola <strong>%s</strong>,</p>
                                <p style="color: #555; font-size: 14px;">Recibimos una solicitud para restablecer tu contraseña.</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="display: inline-block; background-color: #FDD835; color: #1E3A5F; padding: 14px 40px; text-decoration: none; border-radius: 25px; font-weight: bold;">Restablecer Contraseña</a>
                                </div>
                            </td></tr>
                            <tr><td style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                                <p style="color: #6c757d; font-size: 12px; margin: 0;">© 2025 Fundación Huahuacuna</p>
                            </td></tr>
                        </table>
                    </td></tr>
                </table>
            </body>
            </html>
            """.formatted(userName, resetUrl);
    }

    // ========== MÉTODOS DE BIENVENIDA ==========

    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a Fundación Huahuacuna");
            helper.setText(buildWelcomeEmailHtml(userName), true);

            mailSender.send(message);
            log.info("Email de bienvenida enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida: {}", e.getMessage());
        }
    }

    private String buildWelcomeEmailHtml(String userName) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr><td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px;">
                            <tr><td style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 40px 20px; text-align: center;">
                                <h1 style="color: #FDD835; margin: 0; font-size: 28px;">¡Bienvenido! 🎉</h1>
                            </td></tr>
                            <tr><td style="padding: 40px 30px;">
                                <p style="color: #333; font-size: 16px;">Hola <strong>%s</strong>,</p>
                                <p style="color: #555; font-size: 14px;">¡Gracias por unirte a nuestra comunidad!</p>
                            </td></tr>
                            <tr><td style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                                <p style="color: #6c757d; font-size: 12px; margin: 0;">© 2025 Fundación Huahuacuna</p>
                            </td></tr>
                        </table>
                    </td></tr>
                </table>
            </body>
            </html>
            """.formatted(userName);
    }

    // ========== MÉTODOS PARA SOLICITUDES ==========

    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email enviado a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    public void sendNewApplicationNotification(String adminEmail, String applicantName,
                                               String applicationType, String applicantEmail, String applicantPhone) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmail);
            helper.setSubject("Nueva solicitud de " + applicationType);
            helper.setText(buildNewApplicationNotificationHtml(applicantName, applicationType,
                    applicantEmail, applicantPhone), true);
            mailSender.send(message);
            log.info("Notificación enviada a admin: {}", adminEmail);
        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage());
        }
    }

    private String buildNewApplicationNotificationHtml(String name, String type, String email, String phone) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 30px;">
                    <h2 style="color: #1E3A5F;">📋 Nueva Solicitud de %s</h2>
                    <p><strong>Nombre:</strong> %s</p>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>Teléfono:</strong> %s</p>
                    <a href="%s/dashboard" style="display: inline-block; background: #FDD835; color: #1E3A5F; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin-top: 20px;">Ver en Dashboard</a>
                </div>
            </body>
            </html>
            """, type, name, email, phone, frontendUrl);
    }

    public void sendApplicationConfirmation(String toEmail, String applicantName, String applicationType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de solicitud");
            helper.setText(buildApplicationConfirmationHtml(applicantName, applicationType), true);
            mailSender.send(message);
            log.info("Confirmación enviada a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar confirmación: {}", e.getMessage());
        }
    }

    private String buildApplicationConfirmationHtml(String name, String type) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 30px;">
                    <h2 style="color: #1E3A5F;">✅ Solicitud Recibida</h2>
                    <p>Estimado/a <strong>%s</strong>,</p>
                    <p>Hemos recibido tu solicitud para <strong>%s</strong>.</p>
                    <p>Nuestro equipo la revisará pronto y te contactaremos.</p>
                    <p style="margin-top: 30px;">Atentamente,<br><strong>Equipo Fundación Huahuacuna</strong></p>
                </div>
            </body>
            </html>
            """, name, type.equals("VOLUNTARIO") ? "ser voluntario/a" : "apadrinar un niño");
    }

    public void sendApplicationStatusUpdate(String toEmail, String applicantName, String status, String comments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Actualización de tu solicitud");
            helper.setText(buildApplicationStatusUpdateHtml(applicantName, status, comments), true);
            mailSender.send(message);
            log.info("Actualización enviada a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar actualización: {}", e.getMessage());
        }
    }

    private String buildApplicationStatusUpdateHtml(String name, String status, String comments) {
        boolean approved = "APROBADO".equals(status);
        String color = approved ? "#4CAF50" : "#F44336";
        String text = approved ? "Aprobada ✅" : "No Aprobada ❌";
        String msg = approved ? "¡Felicitaciones! Tu solicitud ha sido aprobada."
                : "Tu solicitud no ha sido aprobada en este momento.";

        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 30px;">
                    <h2 style="color: #1E3A5F;">Actualización de Solicitud</h2>
                    <p>Estimado/a <strong>%s</strong>,</p>
                    <div style="background: %s; color: white; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                        <strong>%s</strong>
                    </div>
                    <p>%s</p>
                    %s
                    <p style="margin-top: 30px;">Atentamente,<br><strong>Equipo Fundación Huahuacuna</strong></p>
                </div>
            </body>
            </html>
            """, name, color, text, msg,
                (comments != null && !comments.isEmpty())
                        ? "<div style='background: #f5f5f5; padding: 15px; border-radius: 5px; margin: 15px 0;'><strong>Comentarios:</strong><br>" + comments + "</div>"
                        : "");
    }

    // ========== MÉTODOS PARA DONACIONES ==========

    @Async
    public void sendDonationConfirmation(Donation donation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(donation.getEmail());
            helper.setSubject("¡Gracias por tu donación! - Fundación Huahuacuna");
            helper.setText(buildDonationConfirmationEmail(donation), true);
            mailSender.send(message);
            log.info("Email de donación enviado a: {}", donation.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar email de donación: {}", e.getMessage());
        }
    }

    @Async
    public void sendDonationStatusUpdate(Donation donation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(donation.getEmail());
            helper.setSubject("Actualización de tu donación");
            helper.setText(buildDonationStatusUpdateEmail(donation), true);
            mailSender.send(message);
            log.info("Actualización de donación enviada a: {}", donation.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar actualización de donación: {}", e.getMessage());
        }
    }

    private String buildDonationConfirmationEmail(Donation donation) {
        NumberFormat cf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String details = "";
        if ("monetaria".equals(donation.getDonationType())) {
            details = String.format("<p><strong>Monto:</strong> <span style='color: #1E3A5F; font-size: 18px;'>%s</span></p>%s",
                    cf.format(donation.getAmount()),
                    donation.getPaymentMethod() != null ? "<p><strong>Método:</strong> " + donation.getPaymentMethod() + "</p>" : "");
        } else {
            details = String.format("%s%s",
                    donation.getItemType() != null ? "<p><strong>Tipo:</strong> " + donation.getItemType() + "</p>" : "",
                    donation.getDescription() != null ? "<p><strong>Descripción:</strong> " + donation.getDescription() + "</p>" : "");
        }

        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 30px; text-align: center;">
                        <h1 style="color: #FDD835; margin: 0;">¡Gracias por tu donación! 💛</h1>
                    </div>
                    <div style="padding: 30px;">
                        <p>Estimado/a <strong>%s</strong>,</p>
                        <p>Hemos recibido tu donación. ¡Gracias por tu generosidad!</p>
                        <div style="background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <h3 style="color: #1E3A5F; margin-top: 0;">Detalles:</h3>
                            <p><strong>Fecha:</strong> %s</p>
                            <p><strong>Tipo:</strong> %s</p>
                            %s
                            <p><strong>Estado:</strong> <span style="color: #FDD835;">Pendiente</span></p>
                        </div>
                        <div style="background: #FFF9E6; border-left: 4px solid #FDD835; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; font-size: 13px;">📞 <strong>Contacto:</strong><br>
                            Barrio Uribe Carrera 13 27-34<br>Tel: 312 257 01 41<br>Email: %s</p>
                        </div>
                    </div>
                    <div style="background: #f8f9fa; padding: 20px; text-align: center;">
                        <p style="color: #6c757d; font-size: 12px; margin: 0;">© 2025 Fundación Huahuacuna</p>
                    </div>
                </div>
            </body>
            </html>
            """, donation.getDonorName(), donation.getCreatedAt().format(df),
                donation.getDonationType(), details, fromEmail);
    }

    private String buildDonationStatusUpdateEmail(Donation donation) {
        NumberFormat cf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        String statusText = "confirmed".equals(donation.getStatus()) ? "Confirmada ✅" : "Actualizada";
        String color = "confirmed".equals(donation.getStatus()) ? "#4CAF50" : "#FDD835";
        String amount = "";

        if ("monetaria".equals(donation.getDonationType())) {
            amount = String.format("<p style='text-align: center;'><strong>Monto donado:</strong> <span style='color: #1E3A5F; font-size: 20px;'>%s</span></p>",
                    cf.format(donation.getAmount()));
        }

        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #1E3A5F 0%%, #2C5F7F 100%%); padding: 30px; text-align: center;">
                        <h1 style="color: #FDD835; margin: 0;">Tu donación ha sido %s</h1>
                    </div>
                    <div style="padding: 30px;">
                        <p>Estimado/a <strong>%s</strong>,</p>
                        <p>Tu donación ha sido procesada exitosamente.</p>
                        <div style="background: %s; color: white; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0;">
                            <h3 style="margin: 0;">Estado: %s</h3>
                        </div>
                        %s
                        <p style="text-align: center;">¡Gracias por tu apoyo! 💛</p>
                    </div>
                    <div style="background: #f8f9fa; padding: 20px; text-align: center;">
                        <p style="color: #6c757d; font-size: 12px; margin: 0;">© 2025 Fundación Huahuacuna</p>
                    </div>
                </div>
            </body>
            </html>
            """, statusText, donation.getDonorName(), color, statusText, amount);
    }
}
