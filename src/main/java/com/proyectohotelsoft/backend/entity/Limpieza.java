package com.proyectohotelsoft.backend.entity;

import com.proyectohotelsoft.backend.entity.enums.TipoAseo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "limpiezas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Limpieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Recepcionista que registró la limpieza
    @ManyToOne
    @JoinColumn(name = "id_recepcionista", nullable = false)
    private User recepcionista;

    // Habitación limpiada
    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    // Tipo de aseo (enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAseo tipoAseo;

    // Detalles / observaciones
    @Column(length = 300)
    private String observaciones;

    // Fecha de registro
    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
