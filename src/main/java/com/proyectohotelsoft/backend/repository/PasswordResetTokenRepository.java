package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.PasswordResetToken;
import com.proyectohotelsoft.backend.entity.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de tokens de restablecimiento de contraseña.
 * 
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 * relacionadas con los tokens de recuperación de contraseñas.
 * 
 * @author HotelSoft Team
 * @version 1.0
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Busca un token por su valor único.
     * 
     * @param token Valor del token a buscar
     * @return Optional con el token encontrado o vacío si no existe
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Busca un token asociado a un usuario específico.
     * 
     * @param user Usuario para el cual buscar el token
     * @return Optional con el token encontrado o vacío si no existe
     */
    Optional<PasswordResetToken> findByUser(User user);
    
    /**
     * Elimina todos los tokens asociados a un usuario.
     * 
     * @param user Usuario para el cual eliminar los tokens
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = ?1")
    void deleteByUser(User user);
    
    /**
     * Elimina todos los tokens que han expirado.
     * Operación útil para mantenimiento y limpieza de la base de datos.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}