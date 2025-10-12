package com.proyectohotelsoft.backend.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Entidad que representa un token de restablecimiento de contraseña en el sistema.
 * 
 * Esta entidad almacena tokens temporales utilizados para el proceso de recuperación
 * de contraseñas, garantizando la seguridad mediante fecha de expiración.
 * 
 * @author HotelSoft Team
 * @version 1.0
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private Date expiryDate;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public PasswordResetToken() {
    }

    /**
     * Constructor principal que genera un token único y establece la fecha de expiración.
     * 
     * @param user Usuario asociado al token
     */
    public PasswordResetToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.expiryDate = calculateExpiryDate(60); // 60 minutos de expiración
    }

    /**
     * Calcula la fecha de expiración del token.
     * 
     * @param expiryTimeInMinutes Tiempo de expiración en minutos
     * @return Fecha de expiración calculada
     */
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        return new Date(System.currentTimeMillis() + expiryTimeInMinutes * 60 * 1000);
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}