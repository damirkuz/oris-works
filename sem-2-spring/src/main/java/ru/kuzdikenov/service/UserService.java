package ru.kuzdikenov.service;

import ru.kuzdikenov.dto.CreateUserDto;

public interface UserService {
    void createUser(CreateUserDto createUserDto);

    void verifyUser(String code);
}
