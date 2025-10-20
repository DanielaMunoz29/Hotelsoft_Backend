package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.EmailDTO;

public interface ContactoService {

    void enviarCorreo(EmailDTO emailDTO);

}