package com.proyectohotelsoft.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario en el sistema
 */
@Entity
@Table(name = "users")
@Data
@Getter
@Setter
public class User {

    // Getters y Setters para campos existentes
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
    
    @Column(nullable = false)
    private int puntos;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
    
    // CAMPOS NUEVOS - AGREGAR ESTOS
    @Column(name = "google_id", unique = true, nullable = true)
    private String googleId;
    
    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;
    
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();


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
}