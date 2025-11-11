package com.proyectohotelsoft.backend.configs;

import com.proyectohotelsoft.backend.utils.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración principal de seguridad.
 *
 * - Integra autenticación JWT y OAuth2 con Google.
 * - Gestiona las reglas de acceso a los endpoints.
 * - Configura CORS y la política de sesión (sin estado).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenFilter tokenFilter;

    public SecurityConfig(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    /**
     * Configura la cadena principal de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // --- Configuración CORS / CSRF --- //
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // --- Sesión sin estado (JWT) --- //
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // --- Rutas públicas y protegidas --- //
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/exists/**",
                                "/api/users/register",
                                "/oauth2/**",
                                "/login/**",
                                "/error"
                        ).permitAll()

                        // Protegidas
                        .requestMatchers("/api/habitaciones/crearHabitacion").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones/{numeroHabitacion}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/habitaciones/{numeroHabitacion}/estado").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/habitaciones/{numeroHabitacion}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/habitaciones/{numeroHabitacion}").authenticated()

                        // Permitir GET de habitaciones por estado
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones/estado/{estado}").permitAll()

                        // Cualquier otra ruta
                        .anyRequest().permitAll()
                )

                // --- Configuración OAuth2 (Google) --- //
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("https://hotelsoftback-1495464507.northamerica-northeast1.run.app/login-success", true)
                        .failureUrl("https://hotelsoftback-1495464507.northamerica-northeast1.run.app/login-error")
                )

                // --- Filtro JWT personalizado --- //
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🌍 Orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "https://hotelsoft-3a4b3.web.app",
                "https://hotelsoftback-1495464507.northamerica-northeast1.run.app" // ⚠️ Añadido dominio actual de producción
        ));

        // ✅ Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 📨 Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // 🔍 Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora de caché para preflight

        // Registrar configuración para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean de AuthenticationManager para autenticación manual.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean de codificador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
