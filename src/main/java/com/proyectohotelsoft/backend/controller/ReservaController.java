package com.proyectohotelsoft.backend.controller;

import com.mercadopago.resources.preference.Preference;
import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.exceptions.NoPointsEnoughException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.services.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://hotelsoftback-1495464507.northamerica-northeast1.run.app")
public class ReservaController {

    @Autowired
    private final ReservaService reservaService;


    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO, @RequestParam boolean puntos) {

        try {
            ResponseReservaDTO reservaCreada = reservaService.crearReserva(reservaDTO, puntos);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
        } catch (RuntimeException e){

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
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
    public ResponseEntity<?> eliminarReserva(@PathVariable String id) {
        try{
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException nfe){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", nfe.getMessage()));
        }

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<ResponseReservaDTO>> obtenerPorIdUsuario(@PathVariable Long id, Pageable pageable) {
        Page<ResponseReservaDTO> reservas = reservaService.buscarPorIdUsuario(id, pageable);
        return ResponseEntity.ok(reservas);

    }

    @PostMapping("/realizar-pago")
    public ResponseEntity<Preference> realizarPago(@RequestParam("idReserva") String idReserva) throws Exception {
        return ResponseEntity.ok(reservaService.realizarPagoReserva(idReserva));
    }
}