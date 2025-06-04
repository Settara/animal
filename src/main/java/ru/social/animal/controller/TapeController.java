package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.social.animal.model.Advert;
import ru.social.animal.model.City;
import ru.social.animal.model.Region;
import ru.social.animal.model.User;
import ru.social.animal.service.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Comparator;

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

        List<City> cities = cityService.findAll();
        cities.sort(Comparator.comparing(City::getTitle));
        model.addAttribute("cities", cities);

        List<Region> regions = regionService.findAll();
        regions.sort(Comparator.comparing(Region::getTitle));
        model.addAttribute("regions", regions);

        model.addAttribute("animalTypes", typeOfAnimalService.findAll());

        if (principal != null) {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            model.addAttribute("user", user);
        }

        return "tape";
    }


    @GetMapping("/giveaway")
    public String showGiveAwayTape(@RequestParam(required = false) Long cityId,
                                   @RequestParam(required = false) Long regionId,
                                   @RequestParam(required = false) Long typeOfAnimalId,
                                   Model model,
                                   Principal principal) {

        List<Advert> adverts = advertService.getFilteredGiveAwayAdverts(cityId, regionId, typeOfAnimalId);

        model.addAttribute("adverts", adverts);
        List<City> cities = cityService.findAll();
        cities.sort(Comparator.comparing(City::getTitle));
        model.addAttribute("cities", cities);

        List<Region> regions = regionService.findAll();
        regions.sort(Comparator.comparing(Region::getTitle));
        model.addAttribute("regions", regions);
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());

        if (principal != null) {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            model.addAttribute("user", user);
        }

        return "giveaway";
    }

}
