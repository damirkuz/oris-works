package ru.kuzdikenov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kuzdikenov.dto.AdminNoteDto;
import ru.kuzdikenov.dto.NoteForm;
import ru.kuzdikenov.model.Note;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.NoteRepository;
import ru.kuzdikenov.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void testGetNotesByAuthor() {
        User author = User.builder().username("Damir").build();
        List<Note> notes = List.of(Note.builder().title("note").author(author).build());
        given(userRepository.findByUsername("Damir")).willReturn(Optional.of(author));
        given(noteRepository.findByAuthor(author)).willReturn(notes);

        List<Note> actual = noteService.getNotesByAuthor("Damir");

        assertEquals(notes, actual);
    }

    @Test
    void testGetNotesByAuthorThrowsWhenUserMissing() {
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.getNotesByAuthor("Damir")
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testGetPublicNotesWithQuery() {
        List<Note> notes = List.of(Note.builder().title("java").build());
        given(noteRepository.searchPublic("java")).willReturn(notes);

        List<Note> actual = noteService.getPublicNotes("  java  ");

        assertEquals(notes, actual);
        verify(noteRepository, never()).findByPublishedTrue();
    }

    @Test
    void testGetPublicNotesWithoutQuery() {
        List<Note> notes = List.of(Note.builder().title("public").build());
        given(noteRepository.findByPublishedTrue()).willReturn(notes);

        List<Note> actual = noteService.getPublicNotes("   ");

        assertEquals(notes, actual);
        verify(noteRepository, never()).searchPublic(any());
    }

    @Test
    void testCreateNote() {
        User author = User.builder().username("Damir").build();
        given(userRepository.findByUsername("Damir")).willReturn(Optional.of(author));
        given(noteRepository.save(any(Note.class))).willAnswer(invocation -> invocation.getArgument(0));

        Note created = noteService.createNote(new NoteForm(null, "title", "content", true), "Damir");

        assertEquals("title", created.getTitle());
        assertEquals("content", created.getContent());
        assertTrue(created.isPublished());
        assertSame(author, created.getAuthor());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void testCreateNoteThrowsWhenAuthorMissing() {
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.createNote(new NoteForm(null, "title", "content", true), "Damir")
        );

        assertEquals("Автор не существует", exception.getMessage());
    }

    @Test
    void testGetOwnNote() {
        Note note = Note.builder().id(1L).title("title").build();
        given(noteRepository.findByIdAndAuthorUsername(1L, "Damir")).willReturn(Optional.of(note));

        Note actual = noteService.getOwnNote(1L, "Damir");

        assertSame(note, actual);
    }

    @Test
    void testGetOwnNoteThrowsWhenNotFound() {
        given(noteRepository.findByIdAndAuthorUsername(1L, "Damir")).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.getOwnNote(1L, "Damir")
        );

        assertEquals("Заметка не существует или недоступна", exception.getMessage());
    }

    @Test
    void testEditOwnNote() {
        Note existing = Note.builder()
                .id(1L)
                .title("old")
                .content("old content")
                .published(false)
                .build();
        given(noteRepository.findByIdAndAuthorUsername(1L, "Damir")).willReturn(Optional.of(existing));
        given(noteRepository.save(existing)).willReturn(existing);

        Note updated = noteService.editOwnNote(1L, new NoteForm(null, "new", "new content", true), "Damir");

        assertSame(existing, updated);
        assertEquals("new", existing.getTitle());
        assertEquals("new content", existing.getContent());
        assertTrue(existing.isPublished());
    }

    @Test
    void testEditOwnNoteThrowsWhenNotFound() {
        given(noteRepository.findByIdAndAuthorUsername(1L, "Damir")).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.editOwnNote(1L, new NoteForm(null, "new", "new content", true), "Damir")
        );

        assertEquals("Редактируемая заметка не существует или недоступна", exception.getMessage());
    }

    @Test
    void testDeleteOwnNote() {
        given(noteRepository.deleteByIdAndAuthorUsername(1L, "Damir")).willReturn(1L);

        noteService.deleteOwnNote(1L, "Damir");

        verify(noteRepository).deleteByIdAndAuthorUsername(1L, "Damir");
    }

    @Test
    void testDeleteOwnNoteThrowsWhenNothingDeleted() {
        given(noteRepository.deleteByIdAndAuthorUsername(1L, "Damir")).willReturn(0L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.deleteOwnNote(1L, "Damir")
        );

        assertEquals("Заметка не существует или недоступна", exception.getMessage());
    }

    @Test
    void testGetAllNotesForAdmin() {
        Instant createdAt = Instant.parse("2026-04-11T10:15:30Z");
        User author = User.builder().username("Damir").build();
        Note noteWithAuthor = Note.builder()
                .id(1L)
                .title("title")
                .content("content")
                .createdAt(createdAt)
                .published(true)
                .author(author)
                .build();
        Note noteWithoutAuthor = Note.builder()
                .id(2L)
                .title("orphan")
                .content("content")
                .createdAt(createdAt)
                .published(false)
                .author(null)
                .build();
        given(noteRepository.findAll()).willReturn(List.of(noteWithAuthor, noteWithoutAuthor));

        List<AdminNoteDto> actual = noteService.getAllNotesForAdmin();

        assertEquals(2, actual.size());
        assertEquals("Damir", actual.get(0).authorUsername());
        assertNull(actual.get(1).authorUsername());
        assertFalse(actual.get(1).published());
    }

    @Test
    void testDeleteAnyNote() {
        given(noteRepository.existsById(1L)).willReturn(true);

        noteService.deleteAnyNote(1L);

        verify(noteRepository).deleteById(1L);
    }

    @Test
    void testDeleteAnyNoteThrowsWhenMissing() {
        given(noteRepository.existsById(1L)).willReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.deleteAnyNote(1L)
        );

        assertEquals("Заметка не существует", exception.getMessage());
        verify(noteRepository, never()).deleteById(1L);
    }
}
