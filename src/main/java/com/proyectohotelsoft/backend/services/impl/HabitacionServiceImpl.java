package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.mappers.HabitacionMapper;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.services.HabitacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private final HabitacionRepository habitacionRepository;

    @Autowired
    private final HabitacionMapper habitacionMapper;

    @Override
    @Transactional
    public Habitacion crearHabitacion(HabitacionDTO dto) {

        if (habitacionRepository.findByNumeroHabitacion(dto.numeroHabitacion()).isPresent()) {
            throw new RuntimeException("Esta habitacion ya existe");//TODO cambiar la excepcion
        }

        Habitacion habitacion = habitacionMapper.toEntity(dto);
        return habitacionRepository.save(habitacion);
    }

    @Override
    public Page<ResponseHabitacionDTO> getAll(Pageable pageable) {
        return habitacionRepository.findAll(pageable)
                .map(habitacionMapper::toResponseDTO);
    }

    @Override
    public ResponseHabitacionDTO getByNumero(String numeroHabitacion) {
        Habitacion habitacionEncontrada = habitacionRepository.findByNumeroHabitacion(numeroHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitacion " + numeroHabitacion + " no encontrada"));

        return habitacionMapper.toResponseDTO(habitacionEncontrada);
    }

    @Override
    @Transactional
    public ResponseHabitacionDTO cambiarEstado(String numeroHabitacion, String nuevoEstado) {

        Habitacion habitacion = habitacionRepository.findByNumeroHabitacion(numeroHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitacion " + numeroHabitacion + " no encontrada"));

        try {
            EstadoHabitacion estadoEnum = EstadoHabitacion.fromNombre(nuevoEstado);

            habitacion.setEstado(estadoEnum);
            habitacionRepository.save(habitacion);

            return habitacionMapper.toResponseDTO(habitacion);

        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Estado Invalido: " + nuevoEstado);
        }

    }

    @Override
    @Transactional
    public ResponseHabitacionDTO editarHabitacion(String numeroHabitacion, HabitacionDTO dto) {

        Habitacion habitacion = habitacionRepository.findByNumeroHabitacion(numeroHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitación " + numeroHabitacion + " no encontrada"));

        // Validar duplicado solo si el número nuevo es distinto
        Optional<Habitacion> habitacionExistente = habitacionRepository.findByNumeroHabitacion(dto.numeroHabitacion());
        if (habitacionExistente.isPresent() && !habitacionExistente.get().getNumeroHabitacion().equals(numeroHabitacion)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una habitación con el número " + dto.numeroHabitacion());
        }

        habitacion.setNumeroHabitacion(dto.numeroHabitacion());
        habitacion.setDescripcion(dto.descripcion());
        habitacion.setTipo(TipoHabitacion.fromNombre(dto.tipoHabitacion()));
        habitacion.setPrecio(dto.precio());

        return habitacionMapper.toResponseDTO(habitacion);
    }

    @Override
    public void eliminarHabitacion(String numeroHabitacion) {

        Habitacion habitacion = habitacionRepository.findByNumeroHabitacion(numeroHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitacion " + numeroHabitacion + " no encontrada"));

        habitacionRepository.delete(habitacion);
    }

    @Override
    public Page<ResponseHabitacionDTO> getByTipo(String tipo, Pageable pageable) {
        //TODO capturar exception interna de "fromNombre" y devolver un NotFoundException
        TipoHabitacion tipoEnum = TipoHabitacion.fromNombre(tipo);
        return habitacionRepository.findByTipo(tipoEnum, pageable)
                .map(habitacionMapper::toResponseDTO);
    }

    @Override
    public Page<ResponseHabitacionDTO> getByEstado(String estado, Pageable pageable) {
        //TODO capturar exception interna de "fromNombre" y devolver un NotFoundException
        EstadoHabitacion estadoEnum = EstadoHabitacion.fromNombre(estado);
        return habitacionRepository.findByEstado(estadoEnum, pageable)
                .map(habitacionMapper::toResponseDTO);
    }
}