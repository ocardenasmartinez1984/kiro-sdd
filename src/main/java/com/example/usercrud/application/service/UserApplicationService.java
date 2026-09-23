package com.example.usercrud.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usercrud.application.port.in.CreateUserCommand;
import com.example.usercrud.application.port.in.CreateUserUseCase;
import com.example.usercrud.application.port.in.DeleteUserUseCase;
import com.example.usercrud.application.port.in.GetUserUseCase;
import com.example.usercrud.application.port.in.UpdateUserUseCase;
import com.example.usercrud.application.port.out.UserRepositoryPort;
import com.example.usercrud.domain.exception.DuplicateEmailException;
import com.example.usercrud.domain.exception.UserNotFoundException;
import com.example.usercrud.domain.model.Email;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

@Service
@Transactional
public class UserApplicationService
        implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UserApplicationService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User create(CreateUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException("El email ya está registrado: " + command.email());
        }
        var user = User.crear(command.nombre(), new Email(command.email()), command.activo());
        return userRepositoryPort.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepositoryPort.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepositoryPort.findById(new UserId(id))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    public User update(Long id, CreateUserCommand command) {
        var user = userRepositoryPort.findById(new UserId(id))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));

        if (!user.getEmail().value().equals(command.email())
                && userRepositoryPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException("El email ya está registrado: " + command.email());
        }

        user.cambiarNombre(command.nombre());
        user.cambiarEmail(new Email(command.email()));
        user.setActivo(command.activo() == null ? true : command.activo());

        return userRepositoryPort.save(user);
    }

    @Override
    public void delete(Long id) {
        var userId = new UserId(id);
        if (!userRepositoryPort.existsById(userId)) {
            throw new UserNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepositoryPort.deleteById(userId);
    }
}
