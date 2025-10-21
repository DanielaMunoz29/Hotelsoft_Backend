package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.Comodidad;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import com.proyectohotelsoft.backend.exceptions.AlreadyExistsException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.mappers.HabitacionMapper;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.ReservaRepository;
import com.proyectohotelsoft.backend.services.HabitacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private final HabitacionRepository habitacionRepository;

    @Autowired
    private final HabitacionMapper habitacionMapper;

    private final ReservaRepository reservaRepository;

    @Override
    @Transactional
    public Habitacion crearHabitacion(HabitacionDTO dto) {

        if (habitacionRepository.findByNumeroHabitacion(dto.numeroHabitacion()).isPresent()) {
            throw new AlreadyExistsException("La habitacion numero " + dto.numeroHabitacion() + " ya existe");
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
    public ResponseHabitacionDTO getById(Long idHabitacion) {
        Habitacion habitacionEncontrada = habitacionRepository.findById(idHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitacion con id" + idHabitacion + " no existe"));

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
            throw new AlreadyExistsException("Ya existe una habitación con el número " + dto.numeroHabitacion());
        }

        // Actualizar campos básicos
        habitacion.setNumeroHabitacion(dto.numeroHabitacion());
        habitacion.setNombreHabitacion(dto.nombreHabitacion());
        habitacion.setDescripcion(dto.descripcion());
        habitacion.setTipo(TipoHabitacion.fromNombre(dto.tipoHabitacion()));
        habitacion.setPrecio(dto.precio());

        // Actualizar comodidades
        if (dto.comodidades() != null) {
            Set<Comodidad> nuevasComodidades = dto.comodidades().stream()
                    .map(Comodidad::fromNombre)
                    .collect(Collectors.toSet());
            habitacion.setComodidades(nuevasComodidades);
        }

        // Reemplazar imágenes solo si llegan nuevas
        if (dto.imagenes() != null && !dto.imagenes().isEmpty()) {
            habitacion.setImagenes(new ArrayList<>(dto.imagenes()));
        }

        // Guardar los cambios
        Habitacion actualizada = habitacionRepository.save(habitacion);

        return habitacionMapper.toResponseDTO(actualizada);
    }


    @Override
    public void eliminarHabitacion(String numeroHabitacion) {

        Habitacion habitacion = habitacionRepository.findByNumeroHabitacion(numeroHabitacion)
                .orElseThrow(() -> new NotFoundException("Habitacion " + numeroHabitacion + " no encontrada"));

        habitacionRepository.delete(habitacion);
    }

    @Override
    @Transactional
    public Page<ResponseHabitacionDTO> buscarHabitaciones(
            String tipo,
            String estado,
            LocalDateTime fechaEntrada,
            LocalDateTime fechaSalida,
            Pageable pageable
    ) {
        TipoHabitacion tipoEnum = null;
        EstadoHabitacion estadoEnum = null;

        // Intentar convertir tipo y estado (si vienen)
        if (tipo != null && !tipo.isBlank()) {
            try {
                tipoEnum = TipoHabitacion.fromNombre(tipo);
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Tipo de habitación inválido: " + tipo);
            }
        }

        if (estado != null && !estado.isBlank()) {
            try {
                estadoEnum = EstadoHabitacion.fromNombre(estado);
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Estado de habitación inválido: " + estado);
            }
        }

        // Si no hay fechas, aplicar solo los filtros de tipo/estado
        if (fechaEntrada == null || fechaSalida == null) {
            return habitacionRepository
                    .filtrarPorTipoYEstado(tipoEnum, estadoEnum, pageable)
                    .map(habitacionMapper::toResponseDTO);
        }

        // Validar fechas
        if (fechaSalida.isBefore(fechaEntrada)) {
            throw new IllegalArgumentException("La fecha de salida no puede ser anterior a la fecha de entrada.");
        }

        // Buscar habitaciones ocupadas en ese rango
        List<Long> habitacionesOcupadasIds =
                reservaRepository.findHabitacionesOcupadasEnRango(fechaEntrada, fechaSalida);

        // Buscar habitaciones disponibles aplicando los filtros
        return habitacionRepository
                .filtrarDisponiblesPorTipoEstadoYFechas(tipoEnum, estadoEnum, habitacionesOcupadasIds, pageable)
                .map(habitacionMapper::toResponseDTO);
    }


    @Override
    public Page<ResponseHabitacionDTO> getByEstado(String estado, Pageable pageable) {
        try {
            EstadoHabitacion estadoEnum = EstadoHabitacion.fromNombre(estado);
            return habitacionRepository.findByEstado(estadoEnum, pageable)
                    .map(habitacionMapper::toResponseDTO);
        } catch (Exception e) {
            throw new NotFoundException("Estado invaldo: " + estado);
        }

    }
}