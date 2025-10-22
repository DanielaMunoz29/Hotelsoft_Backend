package com.proyectohotelsoft.backend.services;

import com.proyectohotelsoft.backend.dto.ContactenosDTO;

public interface ContactoService {

    void enviarCorreo(ContactenosDTO request);

}