package com.proyectohotelsoft.backend.mappers;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionMapper {

    // ============================================================
    // 1️⃣ De DTO de entrada → Entidad
    // ============================================================
    public Habitacion toEntity(HabitacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Habitacion habitacion = new Habitacion();
        habitacion.setNumeroHabitacion(dto.numeroHabitacion());
        habitacion.setDescripcion(dto.descripcion());
        habitacion.setPrecio(dto.precio());

        // Los enums se convierten desde String (asegúrate de que los nombres coincidan)
        habitacion.setTipo(TipoHabitacion.valueOf(dto.tipoHabitacion().toUpperCase()));

        // Estado por defecto al crear (si no viene del DTO)
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE); // o el estado inicial que uses

        habitacion.setEnabled(true);
        return habitacion;
    }

    // ============================================================
    // 2️⃣ De Entidad → DTO de respuesta
    // ============================================================
    public ResponseHabitacionDTO toResponseDTO(Habitacion habitacion) {
        if (habitacion == null) {
            return null;
        }

        return new ResponseHabitacionDTO(
                habitacion.getNumeroHabitacion(),
                habitacion.getDescripcion(),
                habitacion.getTipo().name(),     // devuelve el nombre del enum como String
                habitacion.getEstado().name(),   // idem
                String.format("%.2f", habitacion.getPrecio()), // lo pasas a String con 2 decimales
                habitacion.isEnabled()
        );
    }

    // ============================================================
    // 3️⃣ Actualización de entidad existente desde DTO
    // ============================================================
    public void updateEntityFromDTO(HabitacionDTO dto, Habitacion habitacion) {
        if (dto == null || habitacion == null) return;

        habitacion.setDescripcion(dto.descripcion());
        habitacion.setPrecio(dto.precio());
        habitacion.setTipo(TipoHabitacion.valueOf(dto.tipoHabitacion().toUpperCase()));
        // El número de habitación normalmente no se cambia (clave lógica)
    }
}
