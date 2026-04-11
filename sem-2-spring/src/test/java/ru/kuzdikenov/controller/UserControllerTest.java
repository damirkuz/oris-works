package ru.kuzdikenov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.service.UserService;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUsers() throws Exception {
        UserDto userDto = new UserDto("Ivan");
        given(userService.getUsers()).willReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Ivan"));
    }


    @Test
    void testGetUser() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Damir", "parol", "pochta");
        User expectedUser = new User();
        expectedUser.setUsername("Damir");
        expectedUser.setPassword("parol");
        expectedUser.setMail("pochta");

        given(userService.getUser(createUserDto)).willReturn(expectedUser);

        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(user("user"))
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value("Damir"))
                .andExpect(jsonPath("$.password").value("parol"))
                .andExpect(jsonPath("$.mail").value("pochta"));
    }

    @Test
    void testAddUser() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Damir", "parol", "pochta");

        mockMvc.perform(post("/user")
                        .with(user("user"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk());

        verify(userService).createUser(createUserDto);
    }
}
