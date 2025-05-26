package ru.social.animal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/lk")
    public String lk() {
        return "lk";
    }
}
