package ru.kuzdikenov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuzdikenov.service.impl.UserServiceImpl;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserServiceImpl userService;

    public RegistrationController(UserServiceImpl userService) {
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
            Model model
    ) {
        try {
            userService.registerNewUser(username, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", true);
            return "register";
        }
    }
}