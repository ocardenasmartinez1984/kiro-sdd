package com.example.usercrud.infrastructure.adapter.in.web;

import org.springframework.stereotype.Component;

import com.example.usercrud.application.port.in.CreateUserCommand;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.infrastructure.adapter.in.web.dto.UserRequest;
import com.example.usercrud.infrastructure.adapter.in.web.dto.UserResponse;

@Component
public class UserWebMapper {

    public CreateUserCommand toCommand(UserRequest request) {
        return new CreateUserCommand(request.getNombre(), request.getEmail(), request.getActivo());
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId() == null ? null : user.getId().value())
                .nombre(user.getNombre())
                .email(user.getEmail().value())
                .activo(user.isActivo())
                .build();
    }
}
