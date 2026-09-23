package com.example.usercrud.domain.model;

/**
 * Aggregate root del dominio. Modelo puro sin dependencias de Spring ni JPA.
 * Encapsula la lógica de negocio de un usuario.
 */
public class User {

    private UserId id;
    private String nombre;
    private Email email;
    private boolean activo;

    private User(UserId id, String nombre, Email email, boolean activo) {
        this.id = id;
        setNombre(nombre);
        this.email = email;
        this.activo = activo;
    }

    /**
     * Crea un usuario nuevo (aún no persistido). Si {@code activo} es null se
     * activa por defecto.
     */
    public static User crear(String nombre, Email email, Boolean activo) {
        return new User(new UserId(null), nombre, email, activo == null ? true : activo);
    }

    /**
     * Reconstituye un usuario existente desde la capa de persistencia.
     */
    public static User reconstituir(UserId id, String nombre, Email email, boolean activo) {
        return new User(id, nombre, email, activo);
    }

    public void cambiarNombre(String nombre) {
        setNombre(nombre);
    }

    public void cambiarEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        this.email = email;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    private void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        this.nombre = nombre;
    }

    public UserId getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Email getEmail() {
        return email;
    }

    public boolean isActivo() {
        return activo;
    }
}
