package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.*;
import com.proyectohotelsoft.backend.entity.User;

/**
 * Servicio de autenticación que gestiona el registro, login y gestión de usuarios
 */
public interface AuthService {
    
    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param registerUserDTO Datos del usuario a registrar
     * @return Mensaje de confirmación del registro
     */
    String register(RegisterUserDTO registerUserDTO);
    
    /**
     * Autentica un usuario con credenciales email y contraseña
     * 
     * @param loginUserDTO Credenciales de acceso
     * @return Token de autenticación y datos del usuario
     */
    TokenDTO login(LoginUserDTO loginUserDTO);
    
    /**
     * Busca un usuario por su dirección de email
     * 
     * @param email Email del usuario a buscar
     * @return Usuario encontrado
     */
    User findByEmail(String email);
    
    /**
     * Habilita la autenticación de dos factores para un usuario
     * 
     * @param email Email del usuario
     * @return Mensaje de confirmación
     */
    String enableTwoFactor(String email);
    
    /**
     * Deshabilita la autenticación de dos factores para un usuario
     * 
     * @param email Email del usuario
     * @return Mensaje de confirmación
     */
    String disableTwoFactor(String email);
    
    /**
     * Inicia el proceso de recuperación de contraseña
     * 
     * @param email Email del usuario
     * @return Mensaje de confirmación
     */
    String recoverPassword(String email);
    
    /**
     * Verifica el código de autenticación de dos factores
     * 
     * @param verify2FADTO DTO con email y código de verificación
     * @return Token de autenticación
     */
    TokenDTO verifyTwoFactor(Verify2FADTO verify2FADTO);
    
    /**
     * Autentica un usuario usando credenciales de Google
     * 
     * @param googleToken Token de Google OAuth
     * @return Token JWT de la aplicación
     */
    TokenDTO loginWithGoogle(String googleToken);
}