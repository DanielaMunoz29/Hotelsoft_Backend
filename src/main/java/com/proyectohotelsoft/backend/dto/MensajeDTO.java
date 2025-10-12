package com.proyectohotelsoft.backend.dto;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {}
