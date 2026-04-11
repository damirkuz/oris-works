package ru.kuzdikenov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.service.impl.HelloService;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @MockitoBean
    private HelloService helloService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHelloWithName() throws Exception {
        given(helloService.sayHello("Damir")).willReturn("Hello, Damir");

        mockMvc.perform(get("/hello")
                        .param("name", "Damir")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Damir"));
    }

    @Test
    void testHelloWithoutName() throws Exception {
        given(helloService.sayHello(null)).willReturn("Hello, null");

        mockMvc.perform(get("/hello")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, null"));
    }
}
