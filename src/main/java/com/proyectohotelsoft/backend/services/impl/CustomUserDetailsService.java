package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Servicio personalizado para cargar detalles de usuario en Spring Security
 * 
 * Responsabilidades:
 * - Cargar usuarios desde la base de datos por email
 * - Convertir entidades User a UserDetails de Spring Security
 * - Proporcionar autoridades (roles) para autorización
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor para inyección de dependencias
     * 
     * @param userRepository Repositorio de acceso a datos de usuarios
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su dirección de email
     * 
     * @param email Dirección de email del usuario a buscar
     * @return UserDetails con la información del usuario para Spring Security
     * @throws UsernameNotFoundException Si no se encuentra el usuario con el email proporcionado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Convertir entidad User a UserDetails de Spring Security
        return createUserDetails(user);
    }

    /**
     * Convierte una entidad User a UserDetails de Spring Security
     * 
     * @param user Entidad User desde la base de datos
     * @return UserDetails para Spring Security
     */
    private UserDetails createUserDetails(User user) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    /**
     * Obtiene las autoridades (roles) del usuario
     * 
     * @param role Rol del usuario como String
     * @return Colección de autoridades para Spring Security
     */
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        // Asegurar que el rol tenga el formato correcto para Spring Security
        String authority = "ROLE_" + role.toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}