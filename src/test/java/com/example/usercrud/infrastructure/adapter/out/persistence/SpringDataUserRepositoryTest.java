package com.example.usercrud.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SpringDataUserRepositoryTest {

    @Autowired
    private SpringDataUserRepository repository;

    @Test
    void existsByEmailYFindByEmail() {
        var entity = UserJpaEntity.builder()
                .nombre("Juan")
                .email("juan@example.com")
                .activo(true)
                .build();
        repository.save(entity);

        assertThat(repository.existsByEmail("juan@example.com")).isTrue();
        assertThat(repository.existsByEmail("noexiste@example.com")).isFalse();
        assertThat(repository.findByEmail("juan@example.com")).isPresent();
        assertThat(repository.findByEmail("noexiste@example.com")).isEmpty();
    }
}
