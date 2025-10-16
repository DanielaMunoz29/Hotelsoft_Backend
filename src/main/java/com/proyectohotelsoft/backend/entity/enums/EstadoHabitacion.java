package com.proyectohotelsoft.backend.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoHabitacion {

    DISPONIBLE("Disponible"),
    NO_DISPONIBLE("No Disponible"),
    MANTENIMIENTO("Mantenimiento"),
    ASEO("Aseo");

    private final String nombre;

    public static EstadoHabitacion fromNombre(String nombre) {
        for (EstadoHabitacion tipo : values()) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Estado de habitación inválido: " + nombre);
    }
}
