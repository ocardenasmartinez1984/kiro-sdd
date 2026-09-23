package com.example.usercrud.application.port.in;

import com.example.usercrud.domain.model.User;

public interface UpdateUserUseCase {

    User update(Long id, CreateUserCommand command);
}
