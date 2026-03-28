package ru.kuzdikenov.dto;

public record CreateUserDto(
        String username,
        String password,
        String mail
) {

}
