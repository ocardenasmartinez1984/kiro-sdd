package com.example.usercrud.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

/**
 * Puerto de salida para la persistencia del agregado {@link User}.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId id);

    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsById(UserId id);

    void deleteById(UserId id);
}
