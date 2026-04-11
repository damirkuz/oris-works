package ru.kuzdikenov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.dto.NoteForm;
import ru.kuzdikenov.model.Note;
import ru.kuzdikenov.service.impl.NoteService;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @MockitoBean
    private NoteService noteService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testMyNotes() throws Exception {
        Note note = Note.builder()
                .id(1L)
                .title("title")
                .content("content")
                .createdAt(Instant.parse("2026-04-11T10:15:30Z"))
                .published(true)
                .build();
        given(noteService.getNotesByAuthor("Damir")).willReturn(List.of(note));

        mockMvc.perform(get("/notes")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", List.of(note)));
    }

    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/notes/create")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("formTitle", "Создание заметки"))
                .andExpect(model().attribute("actionUrl", "/notes/create"))
                .andExpect(model().attributeExists("note"));
    }

    @Test
    void testCreateNote() throws Exception {
        mockMvc.perform(post("/notes/create")
                        .with(csrf())
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER")))
                        .param("title", "new title")
                        .param("content", "new content")
                        .param("published", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).createNote(new NoteForm(null, "new title", "new content", true), "Damir");
    }

    @Test
    void testEditForm() throws Exception {
        Note note = Note.builder()
                .id(5L)
                .title("old title")
                .content("old content")
                .published(false)
                .build();
        given(noteService.getOwnNote(5L, "Damir")).willReturn(note);

        mockMvc.perform(get("/notes/5/edit")
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("formTitle", "Редактирование заметки"))
                .andExpect(model().attribute("actionUrl", "/notes/5/edit"))
                .andExpect(model().attribute("note", note));
    }

    @Test
    void testEditNote() throws Exception {
        mockMvc.perform(post("/notes/5/edit")
                        .with(csrf())
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER")))
                        .param("title", "updated title")
                        .param("content", "updated content")
                        .param("published", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).editOwnNote(5L, new NoteForm(5L, "updated title", "updated content", false), "Damir");
    }

    @Test
    void testDeleteNote() throws Exception {
        mockMvc.perform(post("/notes/5/delete")
                        .with(csrf())
                        .with(user("Damir").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).deleteOwnNote(5L, "Damir");
    }

    @Test
    void testPublicNotesWithoutQuery() throws Exception {
        Note note = Note.builder().id(1L).title("public").content("visible").published(true).build();
        given(noteService.getPublicNotes(null)).willReturn(List.of(note));

        mockMvc.perform(get("/notes/public").with(user("Damir")))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attribute("notes", List.of(note)))
                .andExpect(model().attribute("query", ""));
    }

    @Test
    void testPublicNotesWithQuery() throws Exception {
        Note note = Note.builder().id(1L).title("java").content("spring").published(true).build();
        given(noteService.getPublicNotes("java")).willReturn(List.of(note));

        mockMvc.perform(get("/notes/public")
                        .param("q", "java")
                        .with(user("Damir")))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attribute("notes", List.of(note)))
                .andExpect(model().attribute("query", "java"));
    }
}
