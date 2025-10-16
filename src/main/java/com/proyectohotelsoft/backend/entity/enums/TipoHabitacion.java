package com.proyectohotelsoft.backend.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoHabitacion {

    SENCILLA("Sencilla"),
    DOBLE("Doble"),
    SUITE("Suite"),
    FAMILIAR("Familiar");

    private final String nombre;

    public static TipoHabitacion fromNombre(String nombre) {
        for (TipoHabitacion tipo : values()) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de habitación inválido: " + nombre);
    }
}
