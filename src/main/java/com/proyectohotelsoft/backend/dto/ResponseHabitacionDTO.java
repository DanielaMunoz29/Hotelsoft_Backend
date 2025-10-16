package com.proyectohotelsoft.backend.dto;

public record ResponseHabitacionDTO (

    String numeroHabitacion,
    String descripcion,
    String tipoHabitacion,
    String estadoHabitacion,
    String precio,
    boolean enabled

){
}