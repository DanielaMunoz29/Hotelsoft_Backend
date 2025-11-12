package com.proyectohotelsoft.backend.controller;

import com.proyectohotelsoft.backend.dto.*;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.repository.UserRepository;
import com.proyectohotelsoft.backend.services.AuthService;
import com.proyectohotelsoft.backend.services.GoogleAuthService;
import com.proyectohotelsoft.backend.services.impl.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para endpoints de autenticación y recuperación de contraseña
 * Maneja el registro, login, OAuth2 Google y recuperación de credenciales de usuarios
 */
@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "https://hotelsoftback-1495464507.northamerica-northeast1.run.app")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;

    /**
     * Constructor para inyección de dependencias
     * 
     * @param authService Servicio de autenticación
     * @param passwordResetService Servicio de recuperación de contraseñas
     * @param googleAuthService Servicio de autenticación con Google
     * @param userRepository Repositorio de usuarios
     */
    public AuthController(AuthService authService, PasswordResetService passwordResetService, 
                         GoogleAuthService googleAuthService, UserRepository userRepository) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.googleAuthService = googleAuthService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint para registro de nuevos usuarios
     * 
     * @param registerUserDTO Datos del usuario a registrar
     * @return Respuesta con resultado del registro
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = authService.register(registerUserDTO);
            
            response.put("success", true);
            response.put("message", result);
            response.put("email", registerUserDTO.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException exception) {
            response.put("success", false);
            response.put("message", exception.getMessage());
            response.put("email", registerUserDTO.getEmail());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception exception) {
            response.put("success", false);
            response.put("message", "Error interno del servidor durante el registro");
            response.put("email", registerUserDTO.getEmail());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para autenticación de usuarios
     * 
     * @param loginUserDTO Credenciales de acceso
     * @return Token de autenticación y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TokenDTO tokenResponse = authService.login(loginUserDTO);
            
            response.put("success", true);
            response.put("token", tokenResponse.getToken());
            response.put("refreshToken", tokenResponse.getRefreshToken());
            response.put("email", tokenResponse.getEmail());
            response.put("nombreCompleto", tokenResponse.getNombreCompleto());
            response.put("message", "Login exitoso");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException exception) {
            response.put("success", false);
            response.put("message", exception.getMessage());
            response.put("email", loginUserDTO.getEmail());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception exception) {
            response.put("success", false);
            response.put("message", "Error interno del servidor durante el login");
            response.put("email", loginUserDTO.getEmail());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para autenticación con Google Identity Services
     * 
     * @param request Mapa que contiene el token ID de Google
     * @return Token JWT de la aplicación
     */
    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> loginWithGoogle(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Token de Google es requerido");
                return ResponseEntity.badRequest().body(response);
            }

            TokenDTO tokenResponse = authService.loginWithGoogle(idToken);
            
            response.put("success", true);
            response.put("token", tokenResponse.getToken());
            response.put("refreshToken", tokenResponse.getRefreshToken());
            response.put("email", tokenResponse.getEmail());
            response.put("nombreCompleto", tokenResponse.getNombreCompleto());
            response.put("message", "Login con Google exitoso");
            
            return ResponseEntity.ok(response);

        } catch (RuntimeException exception) {
            response.put("success", false);
            response.put("message", exception.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception exception) {
            response.put("success", false);
            response.put("message", "Error en autenticación con Google");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint alternativo para autenticación con Google sin validación de token
     * 
     * @param request Mapa que contiene email y nombre del usuario
     * @return Token JWT de la aplicación
     */
    @PostMapping("/google-login-simple")
    public ResponseEntity<Map<String, Object>> loginWithGoogleSimple(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String name = request.get("name");
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email es requerido");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;
            
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                User newUser = new User(
                    email,
                    "google-auth-" + System.currentTimeMillis(),
                    name != null ? name : "Usuario Google",
                    "",
                    "",
                    "USER"
                );
                newUser.setGoogleId("google-id-" + System.currentTimeMillis());
                newUser.setEmailVerified(true);
                user = userRepository.save(newUser);
            }

            String token = "google-token-" + user.getEmail() + "-" + System.currentTimeMillis();
            String refreshToken = "google-refresh-" + user.getEmail() + "-" + System.currentTimeMillis();
            
            response.put("success", true);
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("email", user.getEmail());
            response.put("name", user.getNombreCompleto());
            response.put("message", "Login con Google exitoso");
            
            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            response.put("success", false);
            response.put("message", "Error en autenticación con Google: " + exception.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para obtener información del usuario autenticado via Google OAuth2
     * 
     * @param principal Usuario autenticado por OAuth2
     * @return Información del usuario de Google
     */
    @GetMapping("/google/user")
    public ResponseEntity<Map<String, Object>> getGoogleUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("authenticated", false);
            response.put("message", "Usuario no autenticado via Google");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");
            String picture = principal.getAttribute("picture");
            String googleId = principal.getAttribute("sub");
            
            response.put("authenticated", true);
            response.put("email", email);
            response.put("name", name);
            response.put("picture", picture);
            response.put("googleId", googleId);
            response.put("message", "Información de usuario obtenida correctamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("authenticated", false);
            response.put("message", "Error obteniendo información del usuario de Google");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para verificar estado de autenticación Google OAuth2
     * 
     * @param principal Usuario autenticado por OAuth2
     * @return Estado de autenticación
     */
    @GetMapping("/google/status")
    public ResponseEntity<Map<String, Object>> checkGoogleAuth(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", principal != null);
        
        if (principal != null) {
            response.put("user", Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email")
            ));
            response.put("message", "Usuario autenticado con Google");
        } else {
            response.put("message", "Usuario no autenticado");
        }
        
        return ResponseEntity.ok(response);
    }

 /**
 * Endpoint para solicitar recuperación de contraseña
 * Envía un enlace de recuperación SOLO si el email existe en la base de datos
 * 
 * @param request Mapa que contiene el campo "email"
 * @return Respuesta indicando el resultado de la solicitud
 */
@PostMapping("/forgot-password")
public ResponseEntity<Map<String, Object>> solicitarResetPassword(@RequestBody Map<String, String> request) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El email es requerido");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar si el usuario existe en la base de datos
        Optional<User> userOptional = userRepository.findByEmail(email.trim());
        
        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "No existe una cuenta asociada a este email");
            return ResponseEntity.badRequest().body(response);
        }

        // Iniciar proceso de recuperación en segundo plano para respuesta inmediata
        new Thread(() -> {
            try {
                System.out.println("Procesando solicitud de recuperacion en segundo plano para: " + email);
                passwordResetService.solicitarResetPassword(email.trim());
                System.out.println("Proceso de recuperacion completado para: " + email);
            } catch (RuntimeException e) {
                // Capturar excepción específica de usuario no encontrado
                System.err.println("Usuario no encontrado en proceso background: " + email);
            } catch (Exception e) {
                System.err.println("Error en proceso background para " + email + ": " + e.getMessage());
            }
        }).start();

        // Respuesta inmediata al usuario
        response.put("success", true);
        response.put("message", "Se ha enviado un enlace de recuperación a su email");
        response.put("email", email);
        
        System.out.println("Respuesta enviada inmediatamente al usuario para: " + email);
        
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Error al procesar la solicitud de recuperación de contraseña");
        
        return ResponseEntity.internalServerError().body(response);
    }
}
    /**
     * Endpoint para validar un token de restablecimiento de contraseña
     * Usado cuando el usuario hace clic en el enlace del email
     * 
     * @param token Token de recuperación a validar
     * @return Respuesta indicando si el token es válido
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token == null || token.trim().isEmpty()) {
                response.put("valid", false);
                response.put("success", false);
                response.put("message", "Token no proporcionado");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isValid = passwordResetService.validarToken(token.trim());

            if (isValid) {
                response.put("valid", true);
                response.put("success", true);
                response.put("message", "Token válido");
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("success", false);
                response.put("message", "Token inválido o expirado");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("valid", false);
            response.put("success", false);
            response.put("message", "Error validando el token");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para restablecer la contraseña usando un token válido
     * 
     * @param resetPasswordDTO Objeto con el token y nueva contraseña
     * @return Respuesta con resultado del restablecimiento
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            passwordResetService.cambiarPassword(
                    resetPasswordDTO.getToken(),
                    resetPasswordDTO.getNuevaPassword());

            response.put("success", true);
            response.put("message", "Contraseña actualizada exitosamente");
            
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor al procesar la solicitud");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint administrativo para limpiar tokens expirados manualmente
     * 
     * @return Respuesta con resultado de la operación
     */
    @PostMapping("/cleanup-expired-tokens")
    public ResponseEntity<Map<String, Object>> limpiarTokensExpirados() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            passwordResetService.limpiarTokensExpirados();
            
            response.put("success", true);
            response.put("message", "Limpieza de tokens expirados completada");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la limpieza de tokens: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}