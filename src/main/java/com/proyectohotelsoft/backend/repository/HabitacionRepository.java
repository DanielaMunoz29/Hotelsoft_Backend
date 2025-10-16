package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    Optional<Habitacion> findByNumeroHabitacion(String numeroHabitacion);

    Page<Habitacion> findByTipo(TipoHabitacion tipo, Pageable pageable);

    Page<Habitacion> findByEstado(EstadoHabitacion estado, Pageable pageable);

}