package com.proyectohotelsoft.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de restablecimiento de contraseña.
 * Contiene el token de restablecimiento y la nueva contraseña.
 */
public class ResetPasswordDTO {
    
    @NotBlank(message = "El token es obligatorio")
    private String token;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaPassword;

    /**
     * Obtiene el token de restablecimiento.
     *
     * @return Token de restablecimiento
     */
    public String getToken() { 
        return token; 
    }

    /**
     * Establece el token de restablecimiento.
     *
     * @param token Token de restablecimiento
     */
    public void setToken(String token) { 
        this.token = token; 
    }
    
    /**
     * Obtiene la nueva contraseña.
     *
     * @return Nueva contraseña
     */
    public String getNuevaPassword() { 
        return nuevaPassword; 
    }

    /**
     * Establece la nueva contraseña.
     *
     * @param nuevaPassword Nueva contraseña
     */
    public void setNuevaPassword(String nuevaPassword) { 
        this.nuevaPassword = nuevaPassword; 
    }
}