package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.LimpiezaDto;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Limpieza;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.entity.enums.EstadoLimpieza;
import com.proyectohotelsoft.backend.entity.enums.TipoAseo;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.LimpiezaRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LimpiezaService {

    private final LimpiezaRepository limpiezaRepository;
    private final UserRepository userRepository;
    private final HabitacionRepository habitacionRepository;

    public LimpiezaDto registrarLimpieza(LimpiezaDto dto) {
        System.out.println(dto.idRecepcionista());
        User recepcionista = userRepository.findById(dto.idRecepcionista())
                .orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
        Habitacion habitacion = habitacionRepository.findById(dto.idHabitacion())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        Limpieza limpieza = Limpieza.builder()
                .recepcionista(recepcionista)
                .habitacion(habitacion)
                .tipoAseo(dto.tipoAseo())
                .observaciones(dto.observaciones())
                .fechaRegistro(LocalDateTime.now())
                .estado(EstadoLimpieza.NO_COMPLETADO)
                .build();

        Limpieza saved = limpiezaRepository.save(limpieza);

        return toDto(saved);
    }

    public List<LimpiezaDto> listarLimpiezas() {

        return limpiezaRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LimpiezaDto toDto(Limpieza l) {
        return new LimpiezaDto(
                l.getId(),
                l.getRecepcionista().getId(),
                l.getRecepcionista().getNombreCompleto(),
                l.getHabitacion().getId(),
                l.getHabitacion().getNombreHabitacion(),
                l.getTipoAseo(),
                l.getObservaciones(),
                l.getEstado(),
                l.getFechaRegistro()
        );
    }

    // 🔹 Actualizar limpieza
    public LimpiezaDto actualizarLimpieza(Long id, LimpiezaDto dto) {
        Optional<Limpieza> optionalLimpieza = limpiezaRepository.findById(id);
        if (optionalLimpieza.isEmpty()) {
            throw new RuntimeException("No se encontró la limpieza con ID: " + id);
        }

        Limpieza limpieza = optionalLimpieza.get();

        // Actualizamos solo los campos editables
        if (dto.tipoAseo() != null) {
            limpieza.setTipoAseo(dto.tipoAseo());
        }

        if (dto.observaciones() != null) {
            limpieza.setObservaciones(dto.observaciones());
        }

        if (dto.estado() != null) {
            limpieza.setEstado(dto.estado());
        }

        // Si deseas permitir cambiar la habitación o recepcionista
        if (dto.idHabitacion() != null) {
            Habitacion habitacion = habitacionRepository.findById(dto.idHabitacion())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
            limpieza.setHabitacion(habitacion);
        }

        if (dto.idRecepcionista() != null) {
            User user = userRepository.findById(dto.idRecepcionista())
                    .orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
            limpieza.setRecepcionista(user);
        }

        Limpieza guardada = limpiezaRepository.save(limpieza);
        return toDto(guardada);
    }

    public List<LimpiezaDto> listarLimpiezasPorUsuario(String userId) {
        //List<Limpieza> limpiezas = limpiezaRepository.findAllByRecepcionistaId(userId);
        User usuario = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + userId));

        List<Limpieza> limpiezas = limpiezaRepository.findAllByRecepcionistaId(usuario.getId());

        return limpiezas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
