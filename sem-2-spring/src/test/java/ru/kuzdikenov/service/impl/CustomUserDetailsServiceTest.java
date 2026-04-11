package ru.kuzdikenov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.UserJpaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername() {
        User user = User.builder().username("Damir").password("encoded").build();
        given(userJpaRepository.findByUsername("Damir")).willReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("Damir");

        assertInstanceOf(CustomUserDetails.class, details);
        assertEquals("Damir", details.getUsername());
        assertEquals("encoded", details.getPassword());
    }

    @Test
    void testLoadUserByUsernameThrowsWhenUserMissing() {
        given(userJpaRepository.findByUsername("Damir")).willReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("Damir")
        );

        assertEquals("User Damir not found", exception.getMessage());
    }
}
