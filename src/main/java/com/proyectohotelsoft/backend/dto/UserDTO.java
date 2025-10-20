package com.proyectohotelsoft.backend.dto;

import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de usuario
 * 
 * Utilizado para:
 * - Mostrar información de usuario en respuestas
 * - Actualizar información de usuario
 * - Listar usuarios en el sistema
 */
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String nombreCompleto;
    private String cedula;
    private String telefono;
    private String role;
    private boolean enabled;

    private int puntos;
    
    public UserDTO(Long id, String email, String nombreCompleto,
                   String cedula, String telefono, String role, boolean enabled, int puntos) {
        this.id = id;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        this.telefono = telefono;
        this.role = role;
        this.enabled = enabled;
        this.puntos = puntos;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}