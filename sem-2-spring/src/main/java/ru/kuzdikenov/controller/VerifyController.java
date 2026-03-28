package ru.kuzdikenov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuzdikenov.service.UserService;

@Controller
@RequiredArgsConstructor
public class VerifyController {

    private final UserService userService;

    @GetMapping("/verification")
    public String verify(@RequestParam String code) {
        userService.verifyUser(code);
        return "success_verification";
    }
}
