package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    Optional<Habitacion> findByNumeroHabitacion(String numeroHabitacion);

    Page<Habitacion> findByTipo(TipoHabitacion tipo, Pageable pageable);

    Page<Habitacion> findByEstado(EstadoHabitacion estado, Pageable pageable);


    // Filtro flexible por tipo y estado (sin fechas)
    @Query("""
           SELECT h FROM Habitacion h
           WHERE h.enabled = true
           AND (:tipo IS NULL OR h.tipo = :tipo)
           AND (:estado IS NULL OR h.estado = :estado)
           """)
    Page<Habitacion> filtrarPorTipoYEstado(@Param("tipo") TipoHabitacion tipo,
                                           @Param("estado") EstadoHabitacion estado,
                                           Pageable pageable);

    // Filtro flexible con exclusión de habitaciones ocupadas
    @Query("""
           SELECT h FROM Habitacion h
           WHERE h.enabled = true
           AND (:tipo IS NULL OR h.tipo = :tipo)
           AND (:estado IS NULL OR h.estado = :estado)
           AND (COALESCE(:ocupadas, NULL) IS NULL OR h.id NOT IN :ocupadas)
           """)
    Page<Habitacion> filtrarDisponiblesPorTipoEstadoYFechas(@Param("tipo") TipoHabitacion tipo,
                                                            @Param("estado") EstadoHabitacion estado,
                                                            @Param("ocupadas") List<Long> habitacionesOcupadas,
                                                            Pageable pageable);

}