package ru.kuzdikenov.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzdikenov.dto.AdminNoteDto;
import ru.kuzdikenov.dto.NoteForm;
import ru.kuzdikenov.model.Note;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.NoteRepository;
import ru.kuzdikenov.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Transactional(readOnly = true)
    public List<Note> getNotesByAuthor(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return noteRepository.findByAuthor(user);
    }

    @Transactional(readOnly = true)
    public List<Note> getPublicNotes(String query) {
        if (query != null && !query.isBlank()) {
            return noteRepository.searchPublic(query.trim());
        }
        return noteRepository.findByPublishedTrue();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Transactional
    public Note createNote(NoteForm noteForm, String authorUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Автор не существует"));

        Note note = Note.builder()
                .title(noteForm.title())
                .content(noteForm.content())
                .published(noteForm.published())
                .author(author)
                .createdAt(Instant.now())
                .build();

        return noteRepository.save(note);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Note getOwnNote(Long noteId, String authorUsername) {
        return noteRepository.findByIdAndAuthorUsername(noteId, authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Заметка не существует или недоступна"));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Transactional
    public Note editOwnNote(Long noteId, NoteForm noteForm, String authorUsername) {
        Note note = noteRepository.findByIdAndAuthorUsername(noteId, authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Редактируемая заметка не существует или недоступна"));

        note.setTitle(noteForm.title());
        note.setContent(noteForm.content());
        note.setPublished(noteForm.published());

        return noteRepository.save(note);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Transactional
    public void deleteOwnNote(Long noteId, String authorUsername) {
        long deleted = noteRepository.deleteByIdAndAuthorUsername(noteId, authorUsername);
        if (deleted == 0) {
            throw new IllegalArgumentException("Заметка не существует или недоступна");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional(readOnly = true)
    public List<AdminNoteDto> getAllNotesForAdmin() {
        return noteRepository.findAll().stream()
                .map(note -> new AdminNoteDto(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.isPublished(),
                        note.getAuthor() != null ? note.getAuthor().getUsername() : null
                ))
                .toList();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void deleteAnyNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            throw new IllegalArgumentException("Заметка не существует");
        }
        noteRepository.deleteById(noteId);
    }
}
