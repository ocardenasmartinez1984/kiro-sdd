package com.example.usercrud.domain.model;

import java.util.Objects;

/**
 * Value object que envuelve el identificador de un usuario.
 * Puede tener valor null para usuarios nuevos aún no persistidos.
 * Compara por valor.
 */
public final class UserId {

    private final Long value;

    public UserId(Long value) {
        this.value = value;
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }

    public Long value() {
        return value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
