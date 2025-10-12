package com.proyectohotelsoft.backend.entity;

import jakarta.persistence.*;

/**
 * Entidad que representa un usuario en el sistema
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    
    @Column(unique = true)
    private String cedula;
    
    private String telefono;
    
    private String role;
    
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
    
    // CAMPOS NUEVOS - AGREGAR ESTOS
    @Column(name = "google_id", unique = true, nullable = true)
    private String googleId;
    
    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;
    
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    
    // Constructores
    public User() {}
    
    public User(String email, String password, String nombreCompleto, String cedula, String telefono, String role) {
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        this.telefono = telefono;
        this.role = role;
        this.enabled = true;
        this.twoFactorEnabled = false;
        this.emailVerified = false;
    }
    
    // Getters y Setters para campos existentes
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
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
    
    // GETTERS Y SETTERS PARA CAMPOS NUEVOS - AGREGAR ESTOS
    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }
    
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}