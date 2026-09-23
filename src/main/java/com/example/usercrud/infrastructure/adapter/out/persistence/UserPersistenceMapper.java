package com.example.usercrud.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.example.usercrud.domain.model.Email;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId() == null ? null : user.getId().value())
                .nombre(user.getNombre())
                .email(user.getEmail().value())
                .activo(user.isActivo())
                .build();
    }

    public User toDomain(UserJpaEntity entity) {
        return User.reconstituir(
                new UserId(entity.getId()),
                entity.getNombre(),
                new Email(entity.getEmail()),
                entity.isActivo());
    }
}
