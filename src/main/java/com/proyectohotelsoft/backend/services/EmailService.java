package com.proyectohotelsoft.backend.services;

/**
 * Servicio para el envío de correos electrónicos del sistema.
 * 
 * Define los contratos para:
 * - Envío de correos de recuperación de contraseña
 * - Envío de correos de bienvenida para nuevos usuarios
 * - Envío de códigos de verificación para autenticación en dos factores
 */
public interface EmailService {
    
    /**
     * Envía un correo con enlace para recuperar la contraseña.
     * 
     * @param toEmail Dirección de correo del destinatario
     * @param resetToken Token de recuperación de contraseña
     */
    void sendPasswordResetEmail(String toEmail, String resetToken);
    
    /**
     * Envía un correo de bienvenida a nuevos usuarios registrados.
     * 
     * @param toEmail Dirección de correo del destinatario
     * @param userName Nombre del usuario
     */
    void sendWelcomeEmail(String toEmail, String userName);
    
    /**
     * Envía un código de verificación para autenticación en dos factores.
     * 
     * @param toEmail Dirección de correo del destinatario
     * @param code Código de verificación
     */
    void sendTwoFactorCode(String toEmail, String code);
}