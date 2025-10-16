package com.proyectohotelsoft.backend.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Comodidad {

    CAMA_SENCILLA("Cama sencilla"),
    CAMA_DOBLE("Cama doble"),
    CAMA_QUEEN("Cama queen size"),
    CAMA_KING("Cama king size"),
    TV("Televisión"),
    WIFI("Wi-Fi gratuito"),
    ESCRITORIO("Escritorio"),
    ARMARIO("Armario"),
    TELEFONO("Teléfono"),
    LAMPARAS_LECTURA("Lamparas de lectura"),
    BLACKOUT("Cortinas blackout"),
    MINIBAR("Mini bar"),
    CAJA_FUERTE("Caja fuerte"),
    SECADOR_CABELLO("Secador de cabello"),
    PLANCHA_TABLA_PLANCHAR("Plancha y mesa de planchar"),
    CAFETERA("Cafetera"),
    SERVICIO_LAVANDERIA("Servicio privado de lavandería"),
    BALCON("Balcón privado"),
    JACUZZI("Jacuzzi"),
    SALA_ESTAR("Sala de estar separada"),
    ALBORNOZ_PANTUFLAS("Batas de baño y pantuflas");

    private final String nombre;

    public static Comodidad fromNombre(String nombre) {
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Comodidad inválida: " + nombre));
    }

}