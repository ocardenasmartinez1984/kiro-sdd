package com.example.usercrud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.example.usercrud.domain.exception.InvalidEmailException;

class EmailTest {

    @Test
    void aceptaEmailValido() {
        var email = new Email("juan@example.com");

        assertThat(email.value()).isEqualTo("juan@example.com");
        assertThat(email.getValue()).isEqualTo("juan@example.com");
    }

    @Test
    void lanzaExcepcionConNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void lanzaExcepcionConFormatoInvalido() {
        assertThatThrownBy(() -> new Email("no-es-un-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void equalsYHashCodePorValor() {
        var uno = new Email("juan@example.com");
        var dos = new Email("juan@example.com");
        var otro = new Email("pedro@example.com");

        assertThat(uno).isEqualTo(dos);
        assertThat(uno).hasSameHashCodeAs(dos);
        assertThat(uno).isNotEqualTo(otro);
    }
}
