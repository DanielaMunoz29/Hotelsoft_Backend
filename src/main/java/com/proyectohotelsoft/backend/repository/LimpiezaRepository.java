package com.proyectohotelsoft.backend.repository;

import com.proyectohotelsoft.backend.entity.Limpieza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LimpiezaRepository extends JpaRepository<Limpieza, Long> {


    @Query("SELECT l FROM Limpieza l WHERE l.recepcionista.id = :userId ORDER BY l.fechaRegistro DESC")
    List<Limpieza> findAllByRecepcionistaId(@Param("userId") Long userId);
}
