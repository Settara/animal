package ru.social.animal.controller;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.social.animal.dto.RegisterUserDto;
import ru.social.animal.model.User;
import ru.social.animal.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;


    @GetMapping
    public String showProfilePage(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute RegisterUserDto dto, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email).orElseThrow();
        userService.updateUser(user, dto);
        return "redirect:/profile?success";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают!");
            return "redirect:/profile";
        }

        String email = principal.getName();
        User user = userService.findByEmail(email).orElseThrow();

        if (userService.changePassword(user.getId(), oldPassword, newPassword)) {
            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Старый пароль неверен");
        }

        return "redirect:/profile";
    }
}

