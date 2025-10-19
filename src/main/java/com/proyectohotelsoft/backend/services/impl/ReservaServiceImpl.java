package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Reserva;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import com.proyectohotelsoft.backend.exceptions.AlreadyExistsException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.mappers.HabitacionMapper;
import com.proyectohotelsoft.backend.mappers.ReservaMapper;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.ReservaRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
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

    private final UserRepository userRepository;


    @Override
    @Transactional
    public ResponseReservaDTO crearReserva(ReservaDTO reservaDTO) {

        Habitacion habitacionEncontrada = habitacionRepository.findById(reservaDTO.idHabitacion())
                .orElseThrow(() -> new NotFoundException("Habitacion con id " + reservaDTO.idHabitacion() + " no existe"));

        User userEncontrado = userRepository.findById(reservaDTO.idUsuario())
                .orElseThrow(() -> new NotFoundException("Usuario con id" + reservaDTO.idUsuario() + " no existe"));


        //Validar disponibilidad de la habitación en las fechas solicitadas
        List<Reserva> reservasExistentes = reservaRepository.findByHabitacionId(habitacionEncontrada.getId());
        boolean ocupada = reservasExistentes.stream().anyMatch(r ->
                r.getFechaEntrada().isBefore(reservaDTO.fechaSalida()) &&
                        r.getFechaSalida().isAfter(reservaDTO.fechaEntrada())
        );
        if (ocupada) {
            throw new IllegalStateException("La habitación está ocupada en las fechas seleccionadas.");
        }

        long noches = ChronoUnit.DAYS.between(reservaDTO.fechaEntrada(), reservaDTO.fechaSalida());
        if (noches <= 0) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        // Calcular precio total
        double precioTotal = noches * habitacionEncontrada.getPrecio();

        //Crear la nueva reserva usando el mapper
        Reserva reserva = ReservaMapper.toEntity(reservaDTO, habitacionEncontrada);
        reserva.setUser(userEncontrado);
        reserva.setPrecioTotal(precioTotal);

        //Guardar la reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        //Convertir a ResponseReservaDTO
        ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(habitacionEncontrada);
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

    @Override
    public Page<ResponseReservaDTO> buscarPorIdUsuario(Long id) {
        Page<Reserva> reservasEncontradas = reservaRepository.findAllByUserId(id);
        return reservasEncontradas.map(reserva -> {
            ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(reserva.getHabitacion());
            return reservaMapper.toResponseDTO(reserva, habitacionDTO);
        });
    }
}