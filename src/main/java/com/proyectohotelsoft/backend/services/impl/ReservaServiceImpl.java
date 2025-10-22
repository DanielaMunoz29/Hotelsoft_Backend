package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Reserva;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import com.proyectohotelsoft.backend.exceptions.NoPointsEnoughException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.mappers.HabitacionMapper;
import com.proyectohotelsoft.backend.mappers.ReservaMapper;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.ReservaRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
import com.proyectohotelsoft.backend.services.ReservaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public ResponseReservaDTO crearReserva(ReservaDTO reservaDTO, boolean puntos) {

        try {
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

            // Calcular precio base
            double precioBase = noches * habitacionEncontrada.getPrecio();
            double precioTotal = precioBase;

            int puntosExistentes = userEncontrado.getPuntos();
            int puntosUsados = 0;

            // Si el usuario quiere usar puntos
            if (puntos) {
                if (puntosExistentes <= 0) {
                    throw new NoPointsEnoughException("No tienes puntos disponibles para usar.");
                }

                if (reservaDTO.puntos() > puntosExistentes) {
                    throw new NoPointsEnoughException("No tienes suficientes puntos para usar esa cantidad.");
                }

                puntosUsados = reservaDTO.puntos();
                double descuento = puntosUsados * 1000; // 1 punto = $1000 (según tu lógica)
                precioTotal = Math.max(precioBase - descuento, 0); // evita valores negativos
                userEncontrado.setPuntos(puntosExistentes - puntosUsados);
            }

            // Si no usa puntos, el precio total se mantiene igual
            // Ahora calculamos los puntos que gana por la reserva
            TipoHabitacion tipo = habitacionEncontrada.getTipo();
            int puntosGanados = calcularPuntos(noches, puntosExistentes - puntosUsados, tipo);

            // Sumar los nuevos puntos
            userEncontrado.setPuntos(userEncontrado.getPuntos() + puntosGanados);

            // Crear reserva
            Reserva reserva = ReservaMapper.toEntity(reservaDTO, habitacionEncontrada);
            reserva.setUser(userEncontrado);
            reserva.setPrecioTotal(precioTotal);

            // Guardar en DB
            Reserva reservaGuardada = reservaRepository.save(reserva);

            // Retornar DTO de respuesta
            ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(habitacionEncontrada);
            return reservaMapper.toResponseDTO(reservaGuardada, habitacionDTO);

        } catch (NotFoundException | IllegalArgumentException | IllegalStateException | NoPointsEnoughException e) {
            // Errores esperados (validaciones)
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage(), e);
        } catch (Exception e) {
            // Cualquier otro error inesperado (DB, Mapper, etc.)
            throw new RuntimeException("Ocurrió un error inesperado al crear la reserva.", e);
        }


    }

    private int calcularPuntos(long noches, int puntosRestantes, TipoHabitacion tipo) {

        int puntos = 0;
        switch (tipo){
            case DOBLE -> puntos = puntosRestantes + (2*(int)noches);
            case FAMILIAR -> puntos = puntosRestantes + (3*(int)noches);
            case SENCILLA -> puntos = puntosRestantes + (int)noches;
            case SUITE -> puntos = puntosRestantes + (4*(int)noches);
        }
        return puntos;
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
    public Page<ResponseReservaDTO> buscarPorIdUsuario(Long id, Pageable pageable) {
        Page<Reserva> reservasEncontradas = reservaRepository.findAllByUserId(id, pageable);
        return reservasEncontradas.map(reserva -> {
            ResponseHabitacionDTO habitacionDTO = habitacionMapper.toResponseDTO(reserva.getHabitacion());
            return reservaMapper.toResponseDTO(reserva, habitacionDTO);
        });
    }
}