package ru.kuzdikenov.service;

import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.User;

import java.util.List;

public interface UserService {
    void createUser(CreateUserDto createUserDto);

    void verifyUser(String code);

    User getUser(CreateUserDto createUserDto);

    List<UserDto> getUsers();
}
