package com.proyectohotelsoft.backend.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoReserva {

    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado");

    private final String nombre;

    public static EstadoReserva fromNombre(String nombre) {
        for (EstadoReserva tipo : values()) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Estado de habitación inválido: " + nombre);
    }
}