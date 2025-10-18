package com.proyectohotelsoft.backend.dto;

import java.time.LocalDateTime;

public record ReservaDTO(

    String nombreTitular,
    String email,
    String telefono,
    LocalDateTime fechaEntrada,
    LocalDateTime fechaSalida,
    HabitacionDTO habitacion,
    double precioTotal

) {}