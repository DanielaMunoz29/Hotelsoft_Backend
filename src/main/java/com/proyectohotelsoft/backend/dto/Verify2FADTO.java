package com.proyectohotelsoft.backend.dto;

/**
 * DTO para verificación de autenticación de dos factores
 */
public class Verify2FADTO {
    private String email;
    private String code;

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}