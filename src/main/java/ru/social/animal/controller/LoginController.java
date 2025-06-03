package ru.social.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        if (error != null) {
            Object ex = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (ex != null && ex.toString().contains("UserDetailsService returned null")) {
                model.addAttribute("loginError", "Пользователь с таким email не найден.");
            } else {
                model.addAttribute("loginError", "Неверный пароль.");
            }
        }
        return "login";
    }
}
