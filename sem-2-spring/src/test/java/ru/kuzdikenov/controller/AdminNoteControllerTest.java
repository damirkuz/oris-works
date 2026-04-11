package ru.kuzdikenov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzdikenov.dto.AdminNoteDto;
import ru.kuzdikenov.service.impl.NoteService;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminNoteController.class)
class AdminNoteControllerTest {

    @MockitoBean
    private NoteService noteService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllNotes() throws Exception {
        AdminNoteDto note = new AdminNoteDto(
                1L,
                "title",
                "content",
                Instant.parse("2026-04-11T10:15:30Z"),
                true,
                "Damir"
        );
        given(noteService.getAllNotesForAdmin()).willReturn(List.of(note));

        mockMvc.perform(get("/admin/notes")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].authorUsername").value("Damir"));
    }

    @Test
    void testDeleteNote() throws Exception {
        mockMvc.perform(delete("/admin/notes/7")
                        .with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNoContent());

        verify(noteService).deleteAnyNote(7L);
    }
}
