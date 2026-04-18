package ru.kuzdikenov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.service.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegistrationPage() {
        return "register";
    }

    @PostMapping
    public String registerUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "mail") String mail,
            Model model
    ) {
        try {
            userService.createUser(new CreateUserDto(username, password, mail));
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
