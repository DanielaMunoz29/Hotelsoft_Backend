package com.proyectohotelsoft.backend.controller;

import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.services.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private final ReservaService reservaService;


    @PostMapping
    public ResponseEntity<ResponseReservaDTO> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        ResponseReservaDTO reservaCreada = reservaService.crearReserva(reservaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseReservaDTO>> listarReservas(Pageable pageable) {
        Page<ResponseReservaDTO> reservas = reservaService.listarReservas(pageable);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseReservaDTO> obtenerReserva(@PathVariable String id) {
        ResponseReservaDTO reserva = reservaService.obtenerReserva(id);
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable String id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}