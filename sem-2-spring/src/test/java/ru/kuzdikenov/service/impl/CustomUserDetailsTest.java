package ru.kuzdikenov.service.impl;

import org.junit.jupiter.api.Test;
import ru.kuzdikenov.model.Role;
import ru.kuzdikenov.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomUserDetailsTest {

    @Test
    void testUserDetailsDelegatesToUser() {
        User user = User.builder()
                .username("Damir")
                .password("encoded")
                .roles(List.of(
                        Role.builder().name("USER").build(),
                        Role.builder().name("ADMIN").build()
                ))
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertEquals("Damir", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
    }
}
