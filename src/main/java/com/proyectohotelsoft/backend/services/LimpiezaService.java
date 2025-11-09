package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.LimpiezaDto;
import com.proyectohotelsoft.backend.entity.Habitacion;
import com.proyectohotelsoft.backend.entity.Limpieza;
import com.proyectohotelsoft.backend.entity.User;
import com.proyectohotelsoft.backend.entity.enums.TipoAseo;
import com.proyectohotelsoft.backend.repository.HabitacionRepository;
import com.proyectohotelsoft.backend.repository.LimpiezaRepository;
import com.proyectohotelsoft.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LimpiezaService {

    private final LimpiezaRepository limpiezaRepository;
    private final UserRepository userRepository;
    private final HabitacionRepository habitacionRepository;

    public LimpiezaDto registrarLimpieza(LimpiezaDto dto) {
        User recepcionista = userRepository.findById(dto.idRecepcionista())
                .orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
        Habitacion habitacion = habitacionRepository.findById(dto.idHabitacion())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        Limpieza limpieza = Limpieza.builder()
                .recepcionista(recepcionista)
                .habitacion(habitacion)
                .tipoAseo(dto.tipoAseo())
                .observaciones(dto.observaciones())
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
                l.getFechaRegistro()
        );
    }
    public List<LimpiezaDto> listarLimpiezasPorUsuario(Long userId) {
        List<Limpieza> limpiezas = limpiezaRepository.findAllByRecepcionistaId(userId);
        return limpiezas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
