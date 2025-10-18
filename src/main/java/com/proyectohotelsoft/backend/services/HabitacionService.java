package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.entity.Habitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HabitacionService {

    Habitacion crearHabitacion(HabitacionDTO dto);

    Page<ResponseHabitacionDTO> getAll(Pageable pageable);

    ResponseHabitacionDTO getByNumero(String numeroHabitacion);

    ResponseHabitacionDTO getById(Long idHabitacion);

    ResponseHabitacionDTO cambiarEstado(String numeroHabitacion, String nuevoEstado);

    ResponseHabitacionDTO editarHabitacion(String numeroHabitacion, HabitacionDTO dto);

    void eliminarHabitacion(String numeroHabitacion);

    Page<ResponseHabitacionDTO> getByTipo(String tipo, Pageable pageable);

    Page<ResponseHabitacionDTO> getByEstado(String estado, Pageable pageable);
}