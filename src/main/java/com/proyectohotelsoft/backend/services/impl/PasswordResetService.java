package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.entity.PasswordResetToken;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.repository.PasswordResetTokenRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
import com.proyectohotelsoft.backend.services.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio para la gestión del proceso de recuperación y restablecimiento de
 * contraseñas.
 * 
 * Coordina la generación de tokens, validación y proceso de cambio de
 * contraseña, integrando el servicio de email para notificaciones.
 * 
 * @author HotelSoft Team
 * @version 1.0
 */
@Service
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param tokenRepository Repositorio de tokens de recuperación
     * @param userRepository  Repositorio de usuarios
     * @param emailService    Servicio de envío de correos electrónicos
     * @param passwordEncoder Codificador de contraseñas
     */
    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inicia el proceso de recuperación de contraseña para un email dado.
     * 
     * Este método:
     * 1. Verifica la existencia del usuario
     * 2. Elimina tokens existentes para el usuario
     * 3. Genera un nuevo token de restablecimiento
     * 4. Envía un email con el enlace de recuperación
     * 
     * @param email Email del usuario que solicita el restablecimiento
     * @throws RuntimeException Si el usuario no existe
     */
    public void solicitarResetPassword(String email) {
        System.out.println("=== INICIANDO SOLICITUD DE RESET PASSWORD ===");
        System.out.println("Email recibido: " + email);

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Usuario encontrado: " + user.getEmail());

                // Limpiar tokens existentes
                limpiarTokensExistentes(user);

                // Generar nuevo token
                PasswordResetToken resetToken = new PasswordResetToken(user);
                PasswordResetToken savedToken = tokenRepository.save(resetToken);
                System.out.println("Token generado: " + savedToken.getToken());

                // Enviar email
                try {
                    emailService.sendPasswordResetEmail(user.getEmail(), savedToken.getToken());
                    System.out.println("Email enviado exitosamente");
                } catch (Exception emailException) {
                    System.err.println("Error enviando email: " + emailException.getMessage());
                    // Continuar aunque falle el email
                }

            } else {
                System.out.println("Usuario NO encontrado con email: " + email);
                // LANZAR EXCEPCIÓN PARA DETENER EL PROCESO
                throw new RuntimeException("Usuario no encontrado en la base de datos");
            }

        } catch (RuntimeException e) {
            // Relanzar la excepción para que sea manejada por el controlador
            System.err.println("Error de validacion: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("ERROR CRITICO en solicitarResetPassword:");
            System.err.println("Email: " + email);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error procesando solicitud de recuperacion: " + e.getMessage());
        }

        System.out.println("=== FINALIZADA SOLICITUD ===");
    }

    /**
     * Valida si un token de restablecimiento es válido y no ha expirado.
     *
     * @param token Token a validar
     * @return true si el token es válido y no ha expirado, false en caso contrario
     */
    public boolean validarToken(String token) {
        System.out.println("Validando token: " + token);

        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if (resetToken.isPresent()) {
            PasswordResetToken tokenEntity = resetToken.get();
            if (tokenEntity.isExpired()) {
                System.out.println("Token expirado: " + token);
                tokenRepository.delete(tokenEntity);
                return false;
            }
            System.out.println("Token válido: " + token);
            return true;
        }
        System.out.println("Token no encontrado: " + token);
        return false;
    }

    /**
     * Restablece la contraseña de un usuario usando un token válido.
     * 
     * Este método:
     * 1. Valida el token y verifica que no haya expirado
     * 2. Obtiene el usuario asociado al token
     * 3. Codifica y actualiza la nueva contraseña
     * 4. Elimina el token utilizado
     *
     * @param token         Token de restablecimiento válido
     * @param nuevaPassword Nueva contraseña a establecer
     * @throws RuntimeException Si el token es inválido o ha expirado
     */
    public void cambiarPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de restablecimiento inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token de restablecimiento ha expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    /**
     * Obtiene el usuario asociado a un token válido.
     *
     * @param token Token de restablecimiento válido
     * @return Usuario asociado al token
     * @throws RuntimeException Si el token es inválido o ha expirado
     */
    public User obtenerUsuarioDesdeToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de restablecimiento inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token de restablecimiento ha expirado");
        }

        return resetToken.getUser();
    }

    /**
     * Elimina todos los tokens de restablecimiento existentes para un usuario.
     *
     * @param user Usuario para el cual eliminar los tokens
     */
    private void limpiarTokensExistentes(User user) {
        System.out.println("Buscando tokens existentes para usuario: " + user.getId());

        try {
            // Usa el método deleteByUser que ya tienes en el repository
            tokenRepository.deleteByUser(user);
            System.out.println("Tokens existentes eliminados exitosamente para usuario: " + user.getId());

        } catch (Exception e) {
            System.err.println("Error eliminando tokens existentes: " + e.getMessage());
            // Si falla, intenta con una consulta manual
            try {
                Optional<PasswordResetToken> tokenExistente = tokenRepository.findByUser(user);
                if (tokenExistente.isPresent()) {
                    PasswordResetToken token = tokenExistente.get();
                    System.out.println("Eliminando token existente manualmente: " + token.getToken());
                    tokenRepository.delete(token);
                    System.out.println("Token eliminado exitosamente");
                }
            } catch (Exception e2) {
                System.err.println("Error crítico en limpieza de tokens: " + e2.getMessage());
                throw new RuntimeException("No se pudo limpiar tokens existentes");
            }
        }
    }

    /**
     * Limpia tokens expirados de la base de datos.
     * Método útil para mantenimiento periódico del sistema.
     */
    public void limpiarTokensExpirados() {
        try {
            // Si usas la primera versión sin parámetros:
            tokenRepository.deleteExpiredTokens();
            System.out.println("Limpieza de tokens expirados completada");
        } catch (Exception e) {
            System.err.println("Error en limpieza de tokens expirados: " + e.getMessage());
            // No lanzar excepción para evitar romper el flujo principal
        }
    }

}