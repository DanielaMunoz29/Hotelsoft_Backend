package com.proyectohotelsoft.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailDTO {
    private String asunto;
    private String cuerpo;
    private String destinatario;

}
