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

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenFilter tokenFilter;

    public SecurityConfig(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    /**
     * Configuración principal de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración CORS moderna
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desactivar CSRF (necesario para APIs REST)
                .csrf(csrf -> csrf.disable())
                // Stateless: sin sesiones en el servidor
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/exists/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()

                        // Rutas protegidas
                        .requestMatchers("/api/habitaciones/crearHabitacion").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones/{numeroHabitacion}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones/estado/{estado}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/habitaciones/{numeroHabitacion}/estado").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/habitaciones/{numeroHabitacion}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/habitaciones/{numeroHabitacion}").authenticated()

                        // Todo lo demás permitido
                        .anyRequest().permitAll()
                )
                // Configuración OAuth2 (si usas login con Google)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("https://hotelfront-1495464507.northamerica-northeast1.run.app/login-success", true)
                        .failureUrl("https://hotelfront-1495464507.northamerica-northeast1.run.app/login-error")
                )
                // Filtro JWT personalizado antes del filtro de autenticación
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración CORS para permitir llamadas desde tu frontend (Cloud Run y local)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔹 Agrega el dominio del frontend desplegado
        config.setAllowedOrigins(List.of(
                "https://hotelfront-1495464507.northamerica-northeast1.run.app", // tu frontend en Cloud Run
                "http://localhost:4200" // para desarrollo local
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * Bean para el AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean para encriptar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
