package com.proyectohotelsoft.backend.dto;

/**
 * DTO para recibir el token de Google Sign-In
 */
public class GoogleLoginDTO {
    private String token;
    
    // Constructor vacío
    public GoogleLoginDTO() {}
    
    // Constructor con parámetros
    public GoogleLoginDTO(String token) {
        this.token = token;
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}