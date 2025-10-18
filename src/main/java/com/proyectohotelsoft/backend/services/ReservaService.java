package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.ReservaDTO;
import com.proyectohotelsoft.backend.dto.ResponseReservaDTO;
import com.proyectohotelsoft.backend.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReservaService {

    ResponseReservaDTO crearReserva(ReservaDTO reservaDTO);

    Page<ResponseReservaDTO> listarReservas(Pageable pageable);

    ResponseReservaDTO obtenerReserva(String idReserva);
    
    void eliminarReserva(String id);

}