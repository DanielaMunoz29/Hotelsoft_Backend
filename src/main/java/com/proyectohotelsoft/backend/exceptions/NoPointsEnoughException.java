package com.proyectohotelsoft.backend.exceptions;

public class NoPointsEnoughException extends RuntimeException{

    public NoPointsEnoughException(String mensaje){
        super(mensaje);
    }

}