package com.proyectohotelsoft.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String mensaje){
        super(mensaje);
    }

}