package com.proyectohotelsoft.backend.dto;

import java.time.LocalDateTime;

public record ReservaDTO(

    Long idUsuario,
    Long idHabitacion,
    String nombreTitular,
    String email,
    String telefono,
    LocalDateTime fechaEntrada,
    LocalDateTime fechaSalida
) {}