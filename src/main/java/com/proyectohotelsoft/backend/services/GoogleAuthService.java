package com.proyectohotelsoft.backend.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio para validar tokens de Google OAuth2 y extraer información del usuario
 * Se integra con el sistema de autenticación existente
 */
@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    /**
     * Valida un token ID de Google y extrae la información del usuario
     * 
     * @param idToken Token ID de Google recibido del frontend
     * @return Información del usuario validada
     * @throws Exception Si el token es inválido, expirado o no verificado
     */
    public GoogleUserInfo verifyGoogleToken(String idToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                new GsonFactory()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Token de Google inválido o expirado");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        
        GoogleUserInfo userInfo = new GoogleUserInfo();
        userInfo.setEmail(payload.getEmail());
        userInfo.setName((String) payload.get("name"));
        userInfo.setPicture((String) payload.get("picture"));
        userInfo.setGoogleId(payload.getSubject());
        userInfo.setEmailVerified(Boolean.TRUE.equals(payload.getEmailVerified()));
        
        return userInfo;
    }

    /**
     * Clase interna para transportar información del usuario de Google
     */
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String picture;
        private String googleId;
        private boolean emailVerified;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
        
        public String getGoogleId() { return googleId; }
        public void setGoogleId(String googleId) { this.googleId = googleId; }
        
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    }
}