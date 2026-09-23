package com.example.usercrud.application.port.in;

import com.example.usercrud.domain.model.User;

public interface CreateUserUseCase {

    User create(CreateUserCommand command);
}
