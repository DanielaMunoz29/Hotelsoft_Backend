package com.proyectohotelsoft.backend.mappers;

import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Reserva;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReservaMapper {


    private final UserRepository userRepository;

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
        reserva.setHabitacion(habitacion);
        reserva.setEstado(EstadoReserva.CONFIRMADO); // por defecto o según tu lógica

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