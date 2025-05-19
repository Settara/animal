package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.social.animal.model.User;
import ru.social.animal.service.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/adverts")
// /adverts/create
public class AdvertController {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private CityService cityService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TypeOfAnimalService typeOfAnimalService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String showCreateAdvertPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());
        return "create";
    }


    @PostMapping("/add")
    public String createAdvert(@RequestParam String description,
                               @RequestParam String address,
                               @RequestParam String linkImage,
                               @RequestParam Long cityId,
                               @RequestParam Long regionId,
                               @RequestParam Long typeOfAnimalId,
                               Principal principal) {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login"; // если не найден пользователь
        }

        advertService.createAdvert(description, address, linkImage, cityId, regionId, typeOfAnimalId, userOpt.get());
        return "redirect:/profile"; // после создания возвращаемся в профиль
    }
}

