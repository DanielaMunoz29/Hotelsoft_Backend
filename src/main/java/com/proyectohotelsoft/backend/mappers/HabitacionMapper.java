package com.proyectohotelsoft.backend.mappers;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.Comodidad;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HabitacionMapper {

    public Habitacion toEntity(HabitacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Habitacion habitacion = new Habitacion();
        habitacion.setNumeroHabitacion(dto.numeroHabitacion());
        habitacion.setNombreHabitacion(dto.nombreHabitacion());
        habitacion.setDescripcion(dto.descripcion());
        habitacion.setPrecio(dto.precio());

        // Los enums se convierten desde String
        habitacion.setTipo(TipoHabitacion.valueOf(dto.tipoHabitacion().toUpperCase()));

        // Estado por defecto al crear (si no viene del DTO)
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE); // o el estado inicial que uses

        habitacion.setEnabled(true);

        if (dto.comodidades() != null && !dto.comodidades().isEmpty()) {
            Set<Comodidad> comodidadesEnum = dto.comodidades().stream()
                    .map(nombre -> {
                        try {
                            return Comodidad.fromNombre(nombre);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Comodidad inválida: " + nombre);
                        }
                    })
                    .collect(Collectors.toSet());
            habitacion.setComodidades(comodidadesEnum);
        }

        habitacion.setImagenes(dto.imagenes());
        return habitacion;
    }

    public ResponseHabitacionDTO toResponseDTO(Habitacion habitacion) {
        if (habitacion == null) {
            return null;
        }

        List<String> comodidadesString = habitacion.getComodidades().stream()
                .map(Enum::name)
                .toList();

        return new ResponseHabitacionDTO(
                habitacion.getNumeroHabitacion(),
                habitacion.getNombreHabitacion(),
                habitacion.getDescripcion(),
                habitacion.getTipo().name(),     // devuelve el nombre del enum como String
                habitacion.getEstado().name(),
                habitacion.getPrecio(),
                habitacion.isEnabled(),
                comodidadesString,
                habitacion.getImagenes()
        );
    }
}
