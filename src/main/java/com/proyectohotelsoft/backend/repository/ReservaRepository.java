package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByHabitacionId(Long habitacionId);


    @Query("""
            SELECT DISTINCT r.habitacion.id FROM Reserva r
            WHERE r.fechaEntrada < :fechaSalida AND r.fechaSalida > :fechaEntrada
            """)
    List<Long> findHabitacionesOcupadasEnRango(@Param("fechaEntrada") LocalDateTime fechaEntrada,
                                               @Param("fechaSalida") LocalDateTime fechaSalida);

    @Query("SELECT r FROM Reserva r WHERE r.user.id = :id")
    Page<Reserva> findAllByUserId(Long id, Pageable pageable);
}