package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de usuarios
 * Extiende JpaRepository para obtener operaciones CRUD básicas automáticamente
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de email
     * El email debe ser único en el sistema
     * 
     * @param email Dirección de email del usuario a buscar
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su número de cédula
     * La cédula debe ser única en el sistema
     * 
     * @param cedula Número de cédula del usuario a buscar
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByCedula(String cedula);

    /**
     * Verifica si existe un usuario con el número de cédula especificado
     * 
     * @param cedula Número de cédula a verificar
     * @return true si existe un usuario con esa cédula, false en caso contrario
     */
    boolean existsByCedula(String cedula);

    /**
     * Verifica si existe un usuario con la dirección de email especificada
     * 
     * @param email Dirección de email a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su ID de Google
     * 
     * @param googleId ID único de Google del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByGoogleId(String googleId);
}