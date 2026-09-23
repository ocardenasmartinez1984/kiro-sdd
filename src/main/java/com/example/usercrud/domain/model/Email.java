package com.example.usercrud.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

import com.example.usercrud.domain.exception.InvalidEmailException;

/**
 * Value object inmutable que representa un email válido.
 * Valida el formato en el constructor y compara por valor.
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    public Email(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("El formato del email es inválido: " + value);
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String getValue() {
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
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
