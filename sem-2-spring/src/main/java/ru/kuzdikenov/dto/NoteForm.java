package ru.kuzdikenov.dto;

public record NoteForm(
        Long id,
        String title,
        String content,
        boolean published
) {
}
