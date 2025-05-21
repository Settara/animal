package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.social.animal.model.*;
import ru.social.animal.service.*;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/adverts")
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


    //    @PostMapping("/add")
//    public String createAdvert(@RequestParam String description,
//                               @RequestParam String address,
//                               @RequestParam String linkImage,
//                               @RequestParam Long cityId,
//                               @RequestParam Long regionId,
//                               @RequestParam Long typeOfAnimalId,
//                               Principal principal) {
//
//        Optional<User> userOpt = userService.findByEmail(principal.getName());
//        if (userOpt.isEmpty()) {
//            return "redirect:/login"; // если не найден пользователь
//        }
//
//        advertService.createAdvert(description, address, linkImage, cityId, regionId, typeOfAnimalId, userOpt.get());
//        return "redirect:/profile"; // после создания возвращаемся в профиль
//    }
    @PostMapping("/add")
    public String addAdvert(
            @RequestParam("description") String description,
            @RequestParam("address") String address,
            //Можно создать объявление без фото
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("cityId") Long cityId,
            @RequestParam("regionId") Long regionId,
            @RequestParam("typeOfAnimalId") Long typeOfAnimalId,
            Principal principal
    ) throws IOException {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        // Абсолютный путь к папке ресурсов
        String staticResourcesPath = "D:\\IdeaProjects\\animal\\animal\\src\\main\\resources\\static";
        String uploadsPath = staticResourcesPath + "\\uploads\\advertImages";

        // Создаем директорию, если она не существует
        File userDir = new File(uploadsPath);

        if (!userDir.exists()) {
            if (!userDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + uploadsPath);
            }
        }
        File destinationFilePhoto = new File(userDir, imageFile.getOriginalFilename());

        imageFile.transferTo(destinationFilePhoto);

        // Для использования в приложении сохраняем относительный путь от static
        String relativePath = "/uploads/advertImages/" + imageFile.getOriginalFilename();

        advertService.createAdvert(description, address, relativePath, cityId, regionId, typeOfAnimalId, userOpt.get());
        return "redirect:/tape";
    }


}

