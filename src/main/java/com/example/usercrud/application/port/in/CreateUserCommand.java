package com.example.usercrud.application.port.in;

/**
 * Comando para crear o actualizar un usuario. {@code activo} es opcional
 * (null implica valor por defecto true al crear).
 */
public record CreateUserCommand(String nombre, String email, Boolean activo) {
}
