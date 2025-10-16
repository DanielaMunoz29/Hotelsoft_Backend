package com.proyectohotelsoft.backend.dto;

import java.util.List;

public record HabitacionDTO(

    String numeroHabitacion,

    String nombreHabitacion,
    String descripcion,
    String tipoHabitacion,
    double precio,
    List<String> comodidades
) {
}
