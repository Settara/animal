package ru.social.animal.controller;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.social.animal.dto.RegisterUserDto;
import ru.social.animal.model.User;
import ru.social.animal.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Редактирование своего профиля по Principal
    @GetMapping
    public String editProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile"; // profile.html — страница редактирования
    }

    // Отображение профиля по ID (доступен всем)
    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "profile-view"; // profile-view.html — просмотр профиля
    }

    // Обновление данных
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute RegisterUserDto dto,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();

        try {
            userService.updateUser(user, dto);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        }

        return "redirect:/profile/" + user.getId();
    }


    // Смена пароля
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

        User user = userService.findByEmail(principal.getName()).orElseThrow();

        if (userService.changePassword(user.getId(), oldPassword, newPassword)) {
            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Старый пароль неверен");
        }

        return "redirect:/profile";
    }

    // Загрузка фото профиля
    @PostMapping("/upload-image")
    public String uploadProfileImage(@RequestParam("imageFile") MultipartFile file, Principal principal) throws IOException {
        if (file != null && !file.isEmpty()) {
            User user = userService.findByEmail(principal.getName()).orElseThrow();

            String fileName = file.getOriginalFilename();
            Path workingDir = Paths.get("").toAbsolutePath();
            File userDir = new File(workingDir.toFile(), "user-images/" + user.getEmail());

            if (!userDir.exists() && !userDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + userDir.getAbsolutePath());
            }

            File destinationFile = new File(userDir, fileName);
            file.transferTo(destinationFile);

            String relativePath = "/user-images/" + user.getEmail() + "/" + fileName;
            user.setImageProfile(relativePath);
            userService.save(user);
        }

        User user = userService.findByEmail(principal.getName()).orElseThrow();
        return "redirect:/profile/" + user.getId(); // После загрузки — на просмотр
    }

}
