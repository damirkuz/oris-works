package ru.kuzdikenov.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SecurityConfigIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string("register"));
    }

    @Test
    void testProtectedEndpointRedirectsAnonymousUserToLogin() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void testProtectedEndpointAllowsUserAuthority() throws Exception {
        mockMvc.perform(get("/hello")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    void testAdminEndpointRejectsNonAdmin() throws Exception {
        mockMvc.perform(get("/admin/ping")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminEndpointAllowsAdmin() throws Exception {
        mockMvc.perform(get("/admin/ping")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    void testCsrfIsIgnoredForUserEndpoint() throws Exception {
        mockMvc.perform(post("/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("user-post"));
    }

    @Test
    void testCsrfIsRequiredForProtectedPostEndpoint() throws Exception {
        mockMvc.perform(post("/hello")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isForbidden());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            LiquibaseAutoConfiguration.class
    })
    @Import({SecurityConfig.class, TestEndpoints.class})
    static class TestApplication {
    }

    @RestController
    public static class TestEndpoints {

        @GetMapping("/register")
        public String register() {
            return "register";
        }

        @GetMapping("/hello")
        public String hello() {
            return "hello";
        }

        @PostMapping("/hello")
        public String helloPost() {
            return "hello-post";
        }

        @GetMapping("/admin/ping")
        public String admin() {
            return "admin";
        }

        @PostMapping("/user")
        public String createUser() {
            return "user-post";
        }
    }
}
