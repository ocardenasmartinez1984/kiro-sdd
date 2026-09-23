package com.example.usercrud.application.port.in;

import java.util.List;

import com.example.usercrud.domain.model.User;

public interface GetUserUseCase {

    List<User> findAll();

    User findById(Long id);
}
