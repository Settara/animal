package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.social.animal.model.Advert;
import ru.social.animal.model.User;
import ru.social.animal.service.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TapeController {

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

    @GetMapping("/tape")
    public String showTape(@RequestParam(required = false) Long cityId,
                           @RequestParam(required = false) Long regionId,
                           @RequestParam(required = false) Long typeOfAnimalId,
                           Model model,
                           Principal principal) {

        List<Advert> adverts = advertService.getFilteredAdverts(cityId, regionId, typeOfAnimalId);
        model.addAttribute("adverts", adverts);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());

        if (principal != null) {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            model.addAttribute("user", user);
        }

        return "tape";
    }
}
