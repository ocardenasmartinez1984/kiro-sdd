package com.example.usercrud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void crearConActivoNullQuedaActivo() {
        var user = User.crear("Juan", new Email("juan@example.com"), null);

        assertThat(user.isActivo()).isTrue();
        assertThat(user.getId().value()).isNull();
    }

    @Test
    void crearConActivoFalseQuedaInactivo() {
        var user = User.crear("Juan", new Email("juan@example.com"), false);

        assertThat(user.isActivo()).isFalse();
    }

    @Test
    void cambiarNombreConBlankLanzaExcepcion() {
        var user = User.crear("Juan", new Email("juan@example.com"), true);

        assertThatThrownBy(() -> user.cambiarNombre("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cambiarEmailNullLanzaExcepcion() {
        var user = User.crear("Juan", new Email("juan@example.com"), true);

        assertThatThrownBy(() -> user.cambiarEmail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reconstituirMantieneId() {
        var user = User.reconstituir(new UserId(42L), "Juan", new Email("juan@example.com"), true);

        assertThat(user.getId().value()).isEqualTo(42L);
        assertThat(user.getNombre()).isEqualTo("Juan");
        assertThat(user.getEmail().value()).isEqualTo("juan@example.com");
        assertThat(user.isActivo()).isTrue();
    }
}
