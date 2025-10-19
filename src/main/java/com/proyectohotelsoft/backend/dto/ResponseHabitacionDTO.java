package com.proyectohotelsoft.backend.dto;

import java.util.List;

public record ResponseHabitacionDTO (

    Long idHabitacion,
    String numeroHabitacion,
    String nombreHabitacion,
    String descripcion,
    String tipoHabitacion,
    String estadoHabitacion,
    double precio,
    boolean enabled,
    List<String> comodidades,
    List<String> imagenes

){
}