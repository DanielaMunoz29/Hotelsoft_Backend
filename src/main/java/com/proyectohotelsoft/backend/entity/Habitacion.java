package com.proyectohotelsoft.backend.entity;

import com.proyectohotelsoft.backend.entity.enums.Comodidad;
import com.proyectohotelsoft.backend.entity.enums.EstadoHabitacion;
import com.proyectohotelsoft.backend.entity.enums.TipoHabitacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "habitaciones")
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroHabitacion;

    private String nombreHabitacion;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private TipoHabitacion tipo;

    @Column(nullable = false)
    private EstadoHabitacion estado;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private boolean enabled = true;

    @ElementCollection(targetClass = Comodidad.class)
    @CollectionTable(
        name = "habitacion_comodidades",
        joinColumns = @JoinColumn(name = "habitacion_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "comodidad")
    private Set<Comodidad> comodidades = new HashSet<>();

}