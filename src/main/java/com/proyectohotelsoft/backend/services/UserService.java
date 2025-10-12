package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.RegisterUserDTO;
import com.proyectohotelsoft.backend.dto.UserDTO;
import com.proyectohotelsoft.backend.entity.User;
import java.util.List;

/**
 * Interfaz que define los servicios de gestión de usuarios
 * Proporciona operaciones para administrar usuarios en el sistema
 */
public interface UserService {

    /**
     * Registra un nuevo usuario en el sistema
     * Valida que el email y cédula no estén registrados previamente
     * 
     * @param registerUserDTO Datos del usuario a registrar
     * @return Usuario creado y guardado en la base de datos
     * @throws RuntimeException si el email o cédula ya están registrados
     */
    User registerUser(RegisterUserDTO registerUserDTO);

    /**
     * Obtiene todos los usuarios del sistema
     * 
     * @return Lista de todos los usuarios en formato simplificado
     */
    List<UserDTO> getAllUsers();

    /**
     * Busca un usuario por su identificador único
     * 
     * @param id Número de identificación del usuario
     * @return Información del usuario encontrado
     * @throws RuntimeException si no existe usuario con ese ID
     */
    UserDTO getUserById(Long id);

    /**
     * Busca un usuario por su número de cédula
     * 
     * @param cedula Número de cédula del usuario
     * @return Información del usuario encontrado
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    UserDTO getUserByCedula(String cedula);

    /**
     * Busca un usuario por su dirección de correo electrónico
     * 
     * @param email Dirección de email del usuario
     * @return Información del usuario encontrado
     * @throws RuntimeException si no existe usuario con ese email
     */
    UserDTO getUserByEmail(String email);

    /**
     * Actualiza la información de un usuario usando su ID
     * 
     * @param id      Identificador del usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado
     * @throws RuntimeException si no existe usuario con ese ID
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * Actualiza la información de un usuario usando su cédula
     * 
     * @param cedula  Cédula del usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    UserDTO updateUserByCedula(String cedula, UserDTO userDTO);

    /**
     * Actualiza la información de un usuario usando su email
     * 
     * @param email   Email del usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado
     * @throws RuntimeException si no existe usuario con ese email
     */
    UserDTO updateUserByEmail(String email, UserDTO userDTO);

    /**
     * Elimina un usuario del sistema usando su ID
     * Esta acción no se puede deshacer
     * 
     * @param id Identificador del usuario a eliminar
     * @throws RuntimeException si no existe usuario con ese ID
     */
    void deleteUser(Long id);

    /**
     * Elimina un usuario del sistema usando su cédula
     * Esta acción no se puede deshacer
     * 
     * @param cedula Cédula del usuario a eliminar
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    void deleteUserByCedula(String cedula);

    /**
     * Verifica si existe un usuario con el email especificado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con la cédula especificada
     * 
     * @param cedula Cédula a verificar
     * @return true si existe, false si no existe
     */
    boolean existsByCedula(String cedula);

    /**
     * Activa un usuario que estaba deshabilitado usando su ID
     * 
     * @param id Identificador del usuario a activar
     * @return Usuario activado
     * @throws RuntimeException si no existe usuario con ese ID
     */
    UserDTO enableUser(Long id);

    /**
     * Activa un usuario que estaba deshabilitado usando su cédula
     * 
     * @param cedula Cédula del usuario a activar
     * @return Usuario activado
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    UserDTO enableUserByCedula(String cedula);

    /**
     * Desactiva un usuario usando su ID
     * El usuario no podrá iniciar sesión pero se mantiene en el sistema
     * 
     * @param id Identificador del usuario a desactivar
     * @return Usuario desactivado
     * @throws RuntimeException si no existe usuario con ese ID
     */
    UserDTO disableUser(Long id);

    /**
     * Desactiva un usuario usando su cédula
     * El usuario no podrá iniciar sesión pero se mantiene en el sistema
     * 
     * @param cedula Cédula del usuario a desactivar
     * @return Usuario desactivado
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    UserDTO disableUserByCedula(String cedula);

    /**
     * Cambia el rol de un usuario usando su ID
     * 
     * @param id   Identificador del usuario
     * @param role Nuevo rol a asignar
     * @return Usuario con el rol actualizado
     * @throws RuntimeException si no existe usuario con ese ID
     */
    UserDTO updateUserRole(Long id, String role);

    /**
     * Cambia el rol de un usuario usando su cédula
     * 
     * @param cedula Cédula del usuario
     * @param role   Nuevo rol a asignar
     * @return Usuario con el rol actualizado
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    UserDTO updateUserRoleByCedula(String cedula, String role);

    /**
     * Cambia la contraseña de un usuario identificado por su cédula
     * 
     * @param cedula      Cédula del usuario
     * @param newPassword Nueva contraseña
     * @return true si la contraseña fue cambiada exitosamente
     * @throws RuntimeException si no existe usuario con esa cédula
     */
    boolean changePasswordByCedula(String cedula, String newPassword);

    /**
     * Cambia la contraseña de un usuario identificado por su email
     * 
     * @param email       Email del usuario
     * @param newPassword Nueva contraseña
     * @return true si la contraseña fue cambiada exitosamente
     * @throws RuntimeException si no existe usuario con ese email
     */
    boolean changePasswordByEmail(String email, String newPassword);

    /**
     * Solicita recuperación de contraseña para un email
     * 
     * @param email Email del usuario que solicita recuperación
     * @return true si el proceso fue exitoso
     * @throws RuntimeException si no existe usuario con ese email
     */
    boolean forgotPassword(String email);

    /**
     * Restablece la contraseña usando un token válido
     * 
     * @param token       Token de recuperación
     * @param newPassword Nueva contraseña
     * @return true si la contraseña fue restablecida exitosamente
     * @throws RuntimeException si el token es inválido o expiró
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * Valida si un token de recuperación es válido
     * 
     * @param token Token a validar
     * @return true si el token es válido y no ha expirado
     */
    boolean validatePasswordResetToken(String token);

}