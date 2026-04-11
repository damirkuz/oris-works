package ru.kuzdikenov.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserWithIdAndUsernameDtoTest {

    @Test
    void testRecordAccessors() {
        UserWithIdAndUsernameDto dto = new UserWithIdAndUsernameDto(7L, "Damir");

        assertEquals(7L, dto.id());
        assertEquals("Damir", dto.username());
    }
}
