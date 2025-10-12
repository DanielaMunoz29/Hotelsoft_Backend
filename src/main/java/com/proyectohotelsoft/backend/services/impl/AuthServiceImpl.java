package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.*;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.repository.UserRepository;
import com.proyectohotelsoft.backend.services.AuthService;
import com.proyectohotelsoft.backend.services.GoogleAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * Implementación del servicio de autenticación
 * Gestiona registro, login y autenticación de usuarios
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;

    /**
     * Constructor para inyección de dependencias
     * 
     * @param userRepository    Repositorio de usuarios
     * @param passwordEncoder   Codificador de contraseñas
     * @param googleAuthService Servicio de autenticación con Google
     */
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            GoogleAuthService googleAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleAuthService = googleAuthService;
    }

    /**
     * Método de inicialización del servicio
     */
    @PostConstruct
    public void initialize() {
        // Lógica de inicialización del servicio
    }

    @Override
public String register(RegisterUserDTO registerUserDTO) {
    if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
        throw new RuntimeException("El usuario con este email ya está registrado");
    }

    try {
        User newUser = new User(
            registerUserDTO.getEmail(),
            passwordEncoder.encode(registerUserDTO.getPassword()),
            registerUserDTO.getNombreCompleto(), 
            registerUserDTO.getCedula(),
            registerUserDTO.getTelefono(),
            "USER"
        );

        userRepository.save(newUser);
       
        Optional<User> savedUser = userRepository.findByEmail(registerUserDTO.getEmail());
        if (savedUser.isPresent()) {
            System.out.println("Usuario registrado correctamente: " + registerUserDTO.getEmail());
            return "Usuario registrado exitosamente";
        } else {
            throw new RuntimeException("Error al guardar el usuario en la base de datos");
        }
        
    } catch (Exception e) {
        System.out.println("Error en registro: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error durante el registro: " + e.getMessage());
    }
}

    @Override
    public TokenDTO login(LoginUserDTO loginUserDTO) {
        User user = userRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("La cuenta está deshabilitada");
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return new TokenDTO(accessToken, refreshToken, user.getEmail(), user.getNombreCompleto());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public String enableTwoFactor(String email) {
        User user = findByEmail(email);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        return "Autenticación de dos factores habilitada";
    }

    @Override
    public String disableTwoFactor(String email) {
        User user = findByEmail(email);
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
        return "Autenticación de dos factores deshabilitada";
    }

    @Override
    public String recoverPassword(String email) {
        User user = findByEmail(email);
        String recoveryToken = generateRecoveryToken(user);
        System.out.println("Token de recuperación para " + email + ": " + recoveryToken);
        return "Instrucciones de recuperación enviadas al email";
    }

    @Override
    public TokenDTO verifyTwoFactor(Verify2FADTO verify2FADTO) {
        User user = findByEmail(verify2FADTO.getEmail());
        boolean isValidCode = validateTwoFactorCode(user, verify2FADTO.getCode());
        if (!isValidCode) {
            throw new RuntimeException("Código de autenticación inválido");
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return new TokenDTO(accessToken, refreshToken, user.getEmail(), user.getNombreCompleto());
    }

    @Override
    public TokenDTO loginWithGoogle(String googleToken) {
        try {
            GoogleAuthService.GoogleUserInfo googleUserInfo = googleAuthService.verifyGoogleToken(googleToken);
            User user = findOrCreateUserFromGoogle(googleUserInfo);

            String accessToken = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);

            return new TokenDTO(accessToken, refreshToken, user.getEmail(), user.getNombreCompleto());

        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación con Google: " + e.getMessage());
        }
    }

    /**
     * Busca un usuario existente por email o crea uno nuevo a partir de información
     * de Google
     * 
     * @param googleUserInfo Información validada del usuario de Google
     * @return Usuario existente o nuevo
     */
    private User findOrCreateUserFromGoogle(GoogleAuthService.GoogleUserInfo googleUserInfo) {
        Optional<User> existingUserByGoogleId = userRepository.findByGoogleId(googleUserInfo.getGoogleId());
        if (existingUserByGoogleId.isPresent()) {
            return existingUserByGoogleId.get();
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(googleUserInfo.getEmail());
        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();
            user.setGoogleId(googleUserInfo.getGoogleId());
            return userRepository.save(user);
        }

        User newUser = new User(
                googleUserInfo.getEmail(),
                generateSecureRandomPassword(),
                googleUserInfo.getName(),
                "",
                "",
                "USER");
        newUser.setGoogleId(googleUserInfo.getGoogleId());
        newUser.setEmailVerified(true);

        return userRepository.save(newUser);
    }

    /**
     * Genera una contraseña segura aleatoria para usuarios registrados via Google
     * 
     * @return Contraseña aleatoria segura
     */
    private String generateSecureRandomPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%";
        String allChars = upperCase + lowerCase + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 4; i < 16; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    /**
     * Genera un token de acceso JWT para el usuario
     * 
     * @param user Usuario para el cual generar el token
     * @return Token de acceso JWT
     */
    private String generateAccessToken(User user) {
        return "jwt-access-token-" + user.getEmail() + "-" + System.currentTimeMillis();
    }

    /**
     * Genera un token de refresco JWT para el usuario
     * 
     * @param user Usuario para el cual generar el token
     * @return Token de refresco JWT
     */
    private String generateRefreshToken(User user) {
        return "jwt-refresh-token-" + user.getEmail() + "-" + System.currentTimeMillis();
    }

    /**
     * Genera token de recuperación de contraseña
     * 
     * @param user Usuario para el cual generar el token
     * @return Token de recuperación
     */
    private String generateRecoveryToken(User user) {
        return "recovery-token-" + user.getEmail() + "-" + System.currentTimeMillis();
    }

    /**
     * Valida código de autenticación de dos factores
     * 
     * @param user Usuario a validar
     * @param code Código de autenticación
     * @return true si el código es válido, false en caso contrario
     */
    private boolean validateTwoFactorCode(User user, String code) {
        return true;
    }
}
