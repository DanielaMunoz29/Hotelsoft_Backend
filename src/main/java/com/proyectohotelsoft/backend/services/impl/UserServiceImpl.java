package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.RegisterUserDTO;
import com.proyectohotelsoft.backend.dto.UserDTO;
import com.proyectohotelsoft.backend.entity.PasswordResetToken;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.repository.PasswordResetTokenRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
import com.proyectohotelsoft.backend.services.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios de gestión de usuarios
 * Se encarga de la lógica de negocio relacionada con usuarios
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    /**
     * Constructor para inicializar las dependencias necesarias
     * 
     * @param userRepository  Repositorio para operaciones de base de datos de
     *                        usuarios
     * @param tokenRepository Repositorio para tokens de recuperación de contraseña
     * @param passwordEncoder Codificador para contraseñas seguras
     * @param mailSender      Servicio para envío de correos electrónicos
     */
    public UserServiceImpl(UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public User registerUser(RegisterUserDTO registerUserDTO) {
        // Verificar si el email ya está registrado
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario con este email ya existe");
        }

        // Verificar si la cédula ya está registrada
        if (userRepository.existsByCedula(registerUserDTO.getCedula())) {
            throw new RuntimeException("La cédula ya se encuentra registrada");
        }

        // Crear nuevo usuario con los datos proporcionados
        User user = new User(
                registerUserDTO.getEmail(),
                passwordEncoder.encode(registerUserDTO.getPassword()),
                registerUserDTO.getNombreCompleto(),
                registerUserDTO.getCedula(),
                registerUserDTO.getTelefono(),
                "USER" // Rol por defecto
        );
        user.setPuntos(0);

        return userRepository.save(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByCedula(String cedula) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        return updateUserInformation(user, userDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUserByCedula(String cedula, UserDTO userDTO) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));

        return updateUserInformation(user, userDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUserByEmail(String email, UserDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        return updateUserInformation(user, userDTO);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deleteUserByCedula(String cedula) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByCedula(String cedula) {
        return userRepository.existsByCedula(cedula);
    }

    @Override
    @Transactional
    public UserDTO enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO enableUserByCedula(String cedula) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO disableUserByCedula(String cedula) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUserRoleByCedula(String cedula, String role) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public boolean forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());

        // Por seguridad, siempre devolvemos true aunque el email no exista
        if (userOptional.isEmpty()) {
            return true;
        }

        User user = userOptional.get();

        // Eliminar tokens previos del usuario
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Crear nuevo token
        PasswordResetToken resetToken = new PasswordResetToken(user);
        tokenRepository.save(resetToken);

        // Enviar email
        sendPasswordResetEmail(user.getEmail(), resetToken.getToken(), user.getNombreCompleto());

        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token expirado");
        }

        // Validar longitud de contraseña
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Eliminar token usado
        tokenRepository.delete(resetToken);

        return true;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        return resetToken.isPresent() && !resetToken.get().isExpired();
    }

    /**
     * Envía un correo electrónico con el enlace para restablecer la contraseña
     * 
     * @param to       Dirección de email del destinatario
     * @param token    Token de recuperación de contraseña
     * @param userName Nombre del usuario
     */
    private void sendPasswordResetEmail(String to, String token, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // URL para el frontend
            String resetUrl = "http://localhost:4200/reset-password?token=" + token;

            String emailText = String.format(
                    "Hola %s,\n\n" +
                            "Has solicitado restablecer tu contraseña en HotelSoft.\n\n" +
                            "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n" +
                            "%s\n\n" +
                            "Este enlace expirará en 1 hora.\n\n" +
                            "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                            "Saludos,\nEquipo HotelSoft",
                    userName, resetUrl);

            message.setTo(to);
            message.setSubject("Recuperación de Contraseña - HotelSoft");
            message.setText(emailText);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el email de recuperación: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información básica de un usuario
     * Método auxiliar para evitar duplicación de código
     * 
     * @param user    Usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado en formato DTO
     */
    private UserDTO updateUserInformation(User user, UserDTO userDTO) {
        user.setNombreCompleto(userDTO.getNombreCompleto());
        user.setEmail(userDTO.getEmail());
        user.setCedula(userDTO.getCedula());
        user.setTelefono(userDTO.getTelefono());
        user.setRole(userDTO.getRole());
        user.setEnabled(userDTO.isEnabled());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * Convierte una entidad User a un objeto UserDTO
     * 
     * @param user Entidad User a convertir
     * @return Objeto UserDTO con los datos del usuario
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setNombreCompleto(user.getNombreCompleto());
        userDTO.setCedula(user.getCedula());
        userDTO.setTelefono(user.getTelefono());
        userDTO.setRole(user.getRole());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setPuntos(user.getPuntos());
        return userDTO;
    }

    @Override
    @Transactional
    public boolean changePasswordByCedula(String cedula, String newPassword) {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));

        // Validar longitud de contraseña
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean changePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Validar longitud de contraseña
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

}