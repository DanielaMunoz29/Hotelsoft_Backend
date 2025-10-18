package com.proyectohotelsoft.backend.entity;

import com.proyectohotelsoft.backend.entity.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreTitular;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private LocalDateTime fechaEntrada; //fecha y hora

    @Column(nullable = false)
    private LocalDateTime fechaSalida; //fecha y hora

    @Column(nullable = false)
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @OneToOne
    @JoinColumn(name = "habitacion_id", referencedColumnName = "id")
    private Habitacion habitacion;

}