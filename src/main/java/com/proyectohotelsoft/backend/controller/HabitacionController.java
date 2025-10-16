package com.proyectohotelsoft.backend.controller;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.services.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/habitaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HabitacionController {

    @Autowired
    private final HabitacionService habitacionService;


    @PostMapping //X
    public ResponseEntity<?> crearHabitacion(@RequestBody HabitacionDTO dto) {
        var nuevaHabitacion = habitacionService.crearHabitacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaHabitacion);
    }

    @GetMapping //X
    public ResponseEntity<Page<ResponseHabitacionDTO>> getAll(Pageable pageable) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getAll(pageable);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/{numeroHabitacion}") //X
    public ResponseEntity<ResponseHabitacionDTO> getByNumero(@PathVariable String numeroHabitacion) {
        ResponseHabitacionDTO habitacion = habitacionService.getByNumero(numeroHabitacion);
        return ResponseEntity.ok(habitacion);
    }

    @GetMapping("/tipo/{tipo}") //X
    public ResponseEntity<Page<ResponseHabitacionDTO>> getByTipo(
            @PathVariable String tipo,
            Pageable pageable
    ) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getByTipo(tipo, pageable);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/estado/{estado}") //X
    public ResponseEntity<Page<ResponseHabitacionDTO>> getByEstado(
            @PathVariable String estado,
            Pageable pageable
    ) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getByEstado(estado, pageable);
        return ResponseEntity.ok(habitaciones);
    }

    @PatchMapping("/{numeroHabitacion}/estado") //X
    public ResponseEntity<ResponseHabitacionDTO> cambiarEstado(
            @PathVariable String numeroHabitacion,
            @RequestParam String nuevoEstado
    ) {
        ResponseHabitacionDTO habitacionActualizada =
                habitacionService.cambiarEstado(numeroHabitacion, nuevoEstado);
        return ResponseEntity.ok(habitacionActualizada);
    }

    @PutMapping("/{numeroHabitacion}") //X
    public ResponseEntity<ResponseHabitacionDTO> editarHabitacion(
            @PathVariable String numeroHabitacion,
            @RequestBody HabitacionDTO dto
    ) {
        ResponseHabitacionDTO habitacionActualizada =
                habitacionService.editarHabitacion(numeroHabitacion, dto);
        return ResponseEntity.ok(habitacionActualizada);
    }

    @DeleteMapping("/{numeroHabitacion}") //X
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable String numeroHabitacion) {
        habitacionService.eliminarHabitacion(numeroHabitacion);
        return ResponseEntity.noContent().build();
    }
}