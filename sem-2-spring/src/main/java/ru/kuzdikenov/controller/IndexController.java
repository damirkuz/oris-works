package ru.kuzdikenov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kuzdikenov.aop.Loggable;

@Controller
public class IndexController {
    @Loggable
    @GetMapping(value = "/")
    public String index() {
        return "public_notes";
    }
}
