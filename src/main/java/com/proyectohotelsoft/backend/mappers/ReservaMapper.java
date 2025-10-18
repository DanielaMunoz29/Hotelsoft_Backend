package com.proyectohotelsoft.backend.mappers;

import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Reserva;
import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    // ReservaDTO -> Reserva (para crear una nueva reserva)
    public static Reserva toEntity(ReservaDTO dto, Habitacion habitacion) {
        if (dto == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setNombreTitular(dto.nombreTitular());
        reserva.setEmail(dto.email());
        reserva.setTelefono(dto.telefono());
        reserva.setFechaEntrada(dto.fechaEntrada());
        reserva.setFechaSalida(dto.fechaSalida());
        reserva.setPrecioTotal(dto.precioTotal());
        reserva.setHabitacion(habitacion);
        reserva.setEstado(EstadoReserva.PENDIENTE); // por defecto o según tu lógica

        return reserva;
    }

    // Reserva -> ResponseReservaDTO (para responder al cliente)
    public ResponseReservaDTO toResponseDTO(Reserva reserva, ResponseHabitacionDTO habitacionDTO) {
        if (reserva == null) {
            return null;
        }

        return new ResponseReservaDTO(
            reserva.getNombreTitular(),
            reserva.getEmail(),
            reserva.getTelefono(),
            reserva.getFechaEntrada(),
            reserva.getFechaSalida(),
            habitacionDTO,
            reserva.getPrecioTotal(),
            reserva.getEstado()
        );
    }
}