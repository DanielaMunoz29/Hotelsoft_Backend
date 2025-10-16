package com.proyectohotelsoft.backend.dto;

import java.util.List;

public record ResponseHabitacionDTO (

    String numeroHabitacion,
    String nombreHabitacion,
    String descripcion,
    String tipoHabitacion,
    String estadoHabitacion,
    String precio,
    boolean enabled,
    List<String> comodidades

){
}