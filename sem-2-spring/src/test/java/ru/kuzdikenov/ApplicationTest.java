package ru.kuzdikenov;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class ApplicationTest {

    @Test
    void testMainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.profiles.active=test"};

            Application.main(args);

            springApplication.verify(() -> SpringApplication.run(Application.class, args));
        }
    }
}
