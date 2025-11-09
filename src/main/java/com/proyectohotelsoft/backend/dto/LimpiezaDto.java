package com.proyectohotelsoft.backend.dto;

import com.proyectohotelsoft.backend.entity.enums.TipoAseo;
import java.time.LocalDateTime;

public record LimpiezaDto(
        Long id,
        Long idRecepcionista,
        String nombreRecepcionista,
        Long idHabitacion,
        String nombreHabitacion,
        TipoAseo tipoAseo,
        String observaciones,
        LocalDateTime fechaRegistro
) {}
