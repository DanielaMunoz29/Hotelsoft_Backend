package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Reserva;
import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import com.proyectohotelsoft.backend.exceptions.AlreadyExistsException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.mappers.HabitacionMapper;
import com.proyectohotelsoft.backend.mappers.ReservaMapper;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.ReservaRepository;
import com.proyectohotelsoft.backend.services.ReservaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private final ReservaRepository reservaRepository;

    @Autowired
    private final HabitacionRepository habitacionRepository;

    @Autowired
    private final ReservaMapper reservaMapper;

    @Autowired
    private final HabitacionMapper habitacionMapper;


    @Override
    @Transactional
    public ResponseReservaDTO crearReserva(ReservaDTO reservaDTO) {

        Optional<Habitacion> habitacion = habitacionRepository.findByNumeroHabitacion(reservaDTO.habitacion().numeroHabitacion());
        if (habitacion.isEmpty()) {
            throw new NotFoundException("Habitacion numero " + reservaDTO.habitacion().numeroHabitacion() + " no existe");
        }

        Long habitacionId = habitacion.get().getId();

        //Validar disponibilidad de la habitación en las fechas solicitadas
        List<Reserva> reservasExistentes = reservaRepository.findByHabitacionId(habitacionId);
        boolean ocupada = reservasExistentes.stream().anyMatch(r ->
                r.getFechaEntrada().isBefore(reservaDTO.fechaSalida()) &&
                        r.getFechaSalida().isAfter(reservaDTO.fechaEntrada())
        );
        if (ocupada) {
            throw new IllegalStateException("La habitación está ocupada en las fechas seleccionadas.");
        }

        //Crear la nueva reserva usando el mapper
        Reserva reserva = ReservaMapper.toEntity(reservaDTO, habitacion.get());

        //Guardar la reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        //Convertir a ResponseReservaDTO
        ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(habitacion.get());
        return reservaMapper.toResponseDTO(reservaGuardada, habitacionDTO);
    }

    @Override
    public Page<ResponseReservaDTO> listarReservas(Pageable pageable) {
        Page<Reserva> reservasPage = reservaRepository.findAll(pageable);

        return reservasPage.map(reserva -> {
            ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(reserva.getHabitacion());
            return reservaMapper.toResponseDTO(reserva, habitacionDTO);
        });
    }

    @Override
    public ResponseReservaDTO obtenerReserva(String idReserva) {
        Reserva reserva = reservaRepository.findById(Long.valueOf(idReserva))
                .orElseThrow(() -> new NotFoundException("La reserva con ID " + idReserva + " no existe"));

        ResponseHabitacionDTO habitacionDTO = null;
        if (reserva.getHabitacion() != null) {
            habitacionDTO = habitacionMapper.toResponseDTO(reserva.getHabitacion());
        }

        return reservaMapper.toResponseDTO(reserva, habitacionDTO);
    }


    @Override
    public void eliminarReserva(String id) {

        Reserva reserva = reservaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NotFoundException("La reserva con ID " + id + " no existe"));
        reserva.setEstado(EstadoReserva.CANCELADO);
        reservaRepository.save(reserva);

    }
}