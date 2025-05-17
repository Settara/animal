package ru.social.animal.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.social.animal.dto.RegisterUserDto;
import ru.social.animal.service.UserService;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterUserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(
            @ModelAttribute("user") @Valid RegisterUserDto registerUserDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.userExistsByEmail(registerUserDto.getEmail())) {
            model.addAttribute("account", "Пользователь с таким email уже зарегистрирован.");
            return "register";
        }

        registerUserDto.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        userService.registerNewUser(registerUserDto);

        return "redirect:/login";
    }
}
