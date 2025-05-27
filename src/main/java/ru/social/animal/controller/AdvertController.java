package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.social.animal.model.*;
import ru.social.animal.service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping("/{id}")
    public String getAdvertById(@PathVariable Long id, Model model) {
        Advert advert = advertService.getAdvertById(id);
        model.addAttribute("advert", advert);
        return "advert-details";
    }

    @GetMapping("/create")
    public String showCreateAdvertPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());
        return "create";
    }

    @PostMapping("/add")
    public String addAdvert(
            @RequestParam("description") String description,
            @RequestParam("address") String address,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "noImage", required = false) String noImage,
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

        // Проверка длины на сервере
        //        if (description.length() > 500 || address.length() > 200) {
        //            throw new IllegalArgumentException("Слишком длинное описание или адрес");
        //        }

        String relativePath = null;

        if (noImage == null && imageFile != null && !imageFile.isEmpty()) {
            Path workingDir = Paths.get("").toAbsolutePath();
            File uploadDir = new File(workingDir.toFile(), "uploads/" + user.getId());
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + uploadDir.getAbsolutePath());
            }

            File destinationFile = new File(uploadDir, imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);
            relativePath = "/uploads/" + user.getId() + "/" + imageFile.getOriginalFilename();
        }

        advertService.createAdvert(description, address, relativePath, cityId, regionId, typeOfAnimalId, user);
        return "redirect:/tape";
    }

    // Список «Моих объявлений»
    @GetMapping("/my-adverts")
    public String myAdverts(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        List<Advert> list = advertService.getMyAdverts(user);
        model.addAttribute("adverts", list);
        return "my-adverts";
    }

    // Форма редактирования одного объявления
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        Advert advert = advertService.getAdvertById(id);
        // Можно проверить, что principal — автор объявления
        model.addAttribute("advert", advert);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());
        return "edit-advert";
    }

    // Обработчик сохранения изменений
    @PostMapping("/{id}/edit")
    public String updateAdvert(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam String address,
            @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
            @RequestParam Long cityId,
            @RequestParam Long regionId,
            @RequestParam Long typeOfAnimalId,
            @RequestParam(value="isFound", required=false) Boolean isFound,
            Principal principal
    ) throws IOException {
        Advert advert = advertService.getAdvertById(id);
        // тут можно проверить правомочность principal
        advert.setDescription(description);
        advert.setAddress(address);
        advert.setCity(cityService.findById(cityId).orElse(null));
        advert.setRegion(regionService.findById(regionId).orElse(null));
        advert.setTypeOfAnimal(typeOfAnimalService.findById(typeOfAnimalId).orElse(null));
        advert.setFound(Boolean.TRUE.equals(isFound));

//        // обработка нового файла, если есть…
//        if(imageFile!=null && !imageFile.isEmpty()){
//            String path = storageService.store(imageFile); // ваш код сохранения файла
//            advert.setLinkImage(path);
//        }

        advertService.save(advert);
        return "redirect:/adverts/my-adverts";
    }

}

