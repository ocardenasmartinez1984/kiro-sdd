package com.example.usercrud.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.usercrud.domain.model.Email;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

@DataJpaTest
@Import({ UserPersistenceAdapter.class, UserPersistenceMapper.class })
class UserPersistenceAdapterTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Test
    void saveYFindByIdEnTerminosDelDominio() {
        var user = User.crear("Juan", new Email("juan@example.com"), true);

        var saved = adapter.save(user);

        assertThat(saved.getId().value()).isNotNull();
        var found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail().value()).isEqualTo("juan@example.com");
    }

    @Test
    void existsByEmail() {
        adapter.save(User.crear("Juan", new Email("juan@example.com"), true));

        assertThat(adapter.existsByEmail("juan@example.com")).isTrue();
        assertThat(adapter.existsByEmail("noexiste@example.com")).isFalse();
    }

    @Test
    void existsByIdYDeleteById() {
        var saved = adapter.save(User.crear("Juan", new Email("juan@example.com"), true));
        var id = saved.getId();

        assertThat(adapter.existsById(id)).isTrue();

        adapter.deleteById(id);

        assertThat(adapter.existsById(id)).isFalse();
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByIdInexistenteDevuelveEmpty() {
        assertThat(adapter.findById(new UserId(999L))).isEmpty();
    }
}
