package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de correos electrónicos del sistema.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@hotelsoft.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        System.out.println("✉️  Intentando enviar email de recuperación a: " + toEmail);
        
        try {
            String subject = "HotelSoft - Recuperación de Contraseña";
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            String message = String.format(
                "Hola,\n\n" +
                "Has solicitado recuperar tu contraseña en HotelSoft.\n\n" +
                "Para establecer una nueva contraseña, haz clic en el siguiente enlace:\n" +
                "%s\n\n" +
                "Este enlace expirará en 1 hora.\n\n" +
                "Si no solicitaste este cambio, ignora este correo.\n\n" +
                "Saludos,\nEquipo HotelSoft",
                resetLink
            );

            sendSimpleEmail(toEmail, subject, message);
            System.out.println("✅ Email de recuperación enviado exitosamente a: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico en sendPasswordResetEmail:");
            System.err.println("   Para: " + toEmail);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo enviar el email de recuperación");
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String userName) {
        // Implementación existente...
    }

    @Override
    public void sendTwoFactorCode(String toEmail, String code) {
        // Implementación existente...
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            System.out.println("📧 Configurando email...");
            System.out.println("   From: " + fromEmail);
            System.out.println("   To: " + to);
            System.out.println("   Subject: " + subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            System.out.println("🚀 Enviando email...");
            mailSender.send(message);
            System.out.println("✅ Email enviado exitosamente!");
            
        } catch (Exception e) {
            System.err.println("💥 ERROR enviando email:");
            System.err.println("   Destinatario: " + to);
            System.err.println("   Tipo de error: " + e.getClass().getSimpleName());
            System.err.println("   Mensaje: " + e.getMessage());
            
            // Debug adicional para problemas comunes
            if (e.getMessage().contains("Authentication failed")) {
                System.err.println("   🔐 Problema de autenticación - verifica usuario/contraseña");
            } else if (e.getMessage().contains("Unknown SMTP host")) {
                System.err.println("   🌐 Problema de conexión SMTP - verifica host/puerto");
            } else if (e.getMessage().contains("Connection timed out")) {
                System.err.println("   ⏰ Timeout de conexión - verifica configuración de red");
            }
            
            throw new RuntimeException("Error al enviar correo electrónico: " + e.getMessage());
        }
    }
}