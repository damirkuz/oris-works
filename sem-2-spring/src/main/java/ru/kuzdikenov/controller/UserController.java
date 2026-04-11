package ru.kuzdikenov.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.service.UserService;

import java.util.List;


@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@RequestBody CreateUserDto createUserDto) {
        return userService.getUser(createUserDto);
    }

    @PostMapping("/user")
    public void addUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

//    @DeleteMapping("/user")
//    public void deleteUser(@RequestBody CreateUserDto userWithUsernameDto) {
//        userService.deleteUser(userWithUsernameDto);
//    }
//
//    @PutMapping("/user")
//    public void updateUser(@RequestBody UserWithIdAndUsernameDto userWithIdAndUsernameDto) {
//        userService.updateUser(userWithIdAndUsernameDto);
//    }
//
//
//    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<User> getUsers() {
//        return userService.findAll();
//    }

}
