package ru.kuzdikenov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kuzdikenov.dto.AdminNoteDto;
import ru.kuzdikenov.service.impl.NoteService;

import java.util.List;

@RestController
@RequestMapping("/admin/notes")
@RequiredArgsConstructor
public class AdminNoteController {

    private final NoteService noteService;

    @GetMapping
    public List<AdminNoteDto> getAllNotes() {
        return noteService.getAllNotesForAdmin();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable("id") Long id) {
        noteService.deleteAnyNote(id);
    }
}
