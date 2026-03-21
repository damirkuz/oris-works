package ru.kuzdikenov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kuzdikenov.dto.NoteForm;
import ru.kuzdikenov.model.Note;
import ru.kuzdikenov.service.NoteService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public String myNotes(Principal principal, Model model) {
        List<Note> notes = noteService.getNotesByAuthor(principal.getName());
        model.addAttribute("notes", notes);
        return "notes";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("formTitle", "Создание заметки");
        model.addAttribute("actionUrl", "/notes/create");
        model.addAttribute("note", new Note());
        return "note_form";
    }

    @PostMapping("/create")
    public String createNote(@ModelAttribute NoteForm form, Principal principal) {
        noteService.createNote(form, principal.getName());
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, Principal principal) {
        model.addAttribute("formTitle", "Редактирование заметки");
        model.addAttribute("actionUrl", "/notes/" + id + "/edit");
        model.addAttribute("note", noteService.getOwnNote(id, principal.getName()));
        return "note_form";
    }

    @PostMapping("/{id}/edit")
    public String editNote(@PathVariable("id") Long id,
                           @ModelAttribute NoteForm form,
                           Principal principal) {
        noteService.editOwnNote(id, form, principal.getName());
        return "redirect:/notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable("id") Long id, Principal principal) {
        noteService.deleteOwnNote(id, principal.getName());
        return "redirect:/notes";
    }

    @GetMapping("/public")
    public String publicNotes(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("notes", noteService.getPublicNotes(query));
        model.addAttribute("query", query == null ? "" : query);
        return "public_notes";
    }

}
