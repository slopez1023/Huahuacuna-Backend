package com.huahuacuna.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}