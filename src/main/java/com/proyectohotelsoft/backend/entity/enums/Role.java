package com.proyectohotelsoft.backend.entity.enums;

/**
 * Define los roles disponibles en el sistema HotelSoft.
 * 
 * Los roles determinan los permisos y acceso que tiene cada usuario:
 * - ADMINISTRADOR: Acceso completo al sistema, puede gestionar usuarios, 
 *   habitaciones, reservas y configuraciones.
 * - CLIENTE: Usuario regular que puede realizar reservas, consultar 
 *   habitaciones y gestionar sus propias reservas.
 */
public enum Role {
    ADMIN,
    RECEPTION, 
    USER,
    GUEST
}