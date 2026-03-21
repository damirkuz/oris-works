package ru.kuzdikenov.dto;

import java.time.Instant;

public record AdminNoteDto(
        Long id,
        String title,
        String content,
        Instant createdAt,
        boolean published,
        String authorUsername
) {
}
