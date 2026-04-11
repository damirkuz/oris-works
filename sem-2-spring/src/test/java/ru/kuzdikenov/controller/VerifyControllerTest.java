package ru.kuzdikenov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.service.UserService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(VerifyController.class)
class VerifyControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testVerifyUser() throws Exception {

        doNothing().when(userService).verifyUser("1234");

        mockMvc.perform(get("/verification")
                        .with(user("Damir"))
                        .param("code", "1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("success_verification"));

        verify(userService).verifyUser("1234");
    }

}
