package com.proyectohotelsoft.backend.controller;

import com.proyectohotelsoft.backend.dto.HabitacionDTO;
import com.proyectohotelsoft.backend.dto.ResponseHabitacionDTO;
import com.proyectohotelsoft.backend.exceptions.AlreadyExistsException;
import com.proyectohotelsoft.backend.exceptions.NotFoundException;
import com.proyectohotelsoft.backend.services.CloudinaryService;
import com.proyectohotelsoft.backend.services.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/habitaciones")
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://hotelsoftback-1495464507.northamerica-northeast1.run.app")
@CrossOrigin(origins = "*")
public class HabitacionController {

    @Autowired
    private final HabitacionService habitacionService;

    private final CloudinaryService cloudinaryService;


    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> crearHabitacion(
            @RequestPart("habitacion") HabitacionDTO dto,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes) {

        try {

            List<String> urlsImagenes = new ArrayList<>();
            if (imagenes != null && !imagenes.isEmpty()) {
                urlsImagenes = cloudinaryService.subirMultiplesImagenes(imagenes);
            }

            HabitacionDTO dtoConImagenes = new HabitacionDTO(
                    dto.numeroHabitacion(),
                    dto.nombreHabitacion(),
                    dto.descripcion(),
                    dto.tipoHabitacion(),
                    dto.precio(),
                    dto.comodidades(),
                    urlsImagenes
            );

            var nuevaHabitacion = habitacionService.crearHabitacion(dtoConImagenes);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaHabitacion);

        } catch (AlreadyExistsException ae) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error: ", ae.getMessage()));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al subir imágenes a Cloudinary: " + e.getMessage()));
        }

    }

    @GetMapping
    public ResponseEntity<Page<ResponseHabitacionDTO>> getAll(Pageable pageable) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getAll(pageable);
        return ResponseEntity.ok(habitaciones);
    }

    /*@GetMapping("/{numeroHabitacion}")
    public ResponseEntity<ResponseHabitacionDTO> getByNumero(@PathVariable String numeroHabitacion) {
        ResponseHabitacionDTO habitacion = habitacionService.getByNumero(numeroHabitacion);
        return ResponseEntity.ok(habitacion);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<ResponseHabitacionDTO> getById(@PathVariable Long id) {
        ResponseHabitacionDTO habitacion = habitacionService.getById(id);
        return ResponseEntity.ok(habitacion);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ResponseHabitacionDTO>> buscarHabitaciones(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEntrada,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSalida,
            Pageable pageable
    ) {
        Page<ResponseHabitacionDTO> habitaciones =
                habitacionService.buscarHabitaciones(tipo, estado, fechaEntrada, fechaSalida, pageable);
        return ResponseEntity.ok(habitaciones);
    }

    /*
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<ResponseHabitacionDTO>> getByTipo(
            @PathVariable String tipo,
            Pageable pageable
    ) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getByTipo(tipo, pageable);
        return ResponseEntity.ok(habitaciones);
    }
    */

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<ResponseHabitacionDTO>> getByEstado(
            @PathVariable String estado,
            Pageable pageable
    ) {
        Page<ResponseHabitacionDTO> habitaciones = habitacionService.getByEstado(estado, pageable);
        return ResponseEntity.ok(habitaciones);
    }

    @PatchMapping("/{numeroHabitacion}/estado")
    public ResponseEntity<ResponseHabitacionDTO> cambiarEstado(
            @PathVariable String numeroHabitacion,
            @RequestParam String nuevoEstado
    ) {
        ResponseHabitacionDTO habitacionActualizada =
                habitacionService.cambiarEstado(numeroHabitacion, nuevoEstado);
        return ResponseEntity.ok(habitacionActualizada);
    }

    @PutMapping(value = "/{numeroHabitacion}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> editarHabitacion(
            @PathVariable String numeroHabitacion,
            @RequestPart("habitacion") HabitacionDTO dto,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes
    ) {
        try {
            List<String> urlsImagenes = new ArrayList<>();

            // Si se envían imágenes nuevas, las subimos a Cloudinary
            if (imagenes != null && !imagenes.isEmpty()) {
                urlsImagenes = cloudinaryService.subirMultiplesImagenes(imagenes);
            }

            // Creamos un nuevo DTO combinando los datos previos y las nuevas imágenes
            HabitacionDTO dtoConImagenes = new HabitacionDTO(
                    dto.numeroHabitacion(),
                    dto.nombreHabitacion(),
                    dto.descripcion(),
                    dto.tipoHabitacion(),
                    dto.precio(),
                    dto.comodidades(),
                    urlsImagenes.isEmpty() ? dto.imagenes() : urlsImagenes // mantiene las existentes si no hay nuevas
            );

            ResponseHabitacionDTO habitacionActualizada =
                    habitacionService.editarHabitacion(numeroHabitacion, dtoConImagenes);

            return ResponseEntity.ok(habitacionActualizada);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al subir imágenes a Cloudinary: " + e.getMessage()));

        } catch (AlreadyExistsException ae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ae.getMessage()));

        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", nfe.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{numeroHabitacion}")
    public ResponseEntity<?> eliminarHabitacion(@PathVariable String numeroHabitacion) {

        try {
            habitacionService.eliminarHabitacion(numeroHabitacion);
            return ResponseEntity.noContent().build();

        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", nfe.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));

        }
    }
}