package ru.kuzdikenov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.service.UserService;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRegistrationPage() throws Exception {
        mockMvc.perform(get("/register").with(user("Damir")))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .with(user("Damir"))
                        .with(csrf())
                        .param("username", "Damir")
                        .param("mail", "damir@example.com")
                        .param("password", "parol"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).createUser(new CreateUserDto("Damir", "parol", "damir@example.com"));
    }

    @Test
    void testRegisterUserDuplicateUsername() throws Exception {
        willThrow(new IllegalArgumentException("duplicate"))
                .given(userService).createUser(new CreateUserDto("Damir", "parol", "damir@example.com"));

        mockMvc.perform(post("/register")
                        .with(user("Damir"))
                        .with(csrf())
                        .param("username", "Damir")
                        .param("mail", "damir@example.com")
                        .param("password", "parol"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("errorMessage", "duplicate"));
    }
}
