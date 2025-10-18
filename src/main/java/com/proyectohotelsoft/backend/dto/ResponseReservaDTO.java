package com.proyectohotelsoft.backend.dto;

import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import java.time.LocalDateTime;

public record ResponseReservaDTO(

        String nombreTitular,
        String email,
        String telefono,
        LocalDateTime fechaEntrada,
        LocalDateTime fechaSalida,
        ResponseHabitacionDTO habitacion,
        double precioTotal,
        EstadoReserva estadoReserva

) {}