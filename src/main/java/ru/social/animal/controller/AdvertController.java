package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.social.animal.model.*;
import ru.social.animal.repository.AdvertRepo;
import ru.social.animal.repository.CityRepo;
import ru.social.animal.repository.RegionRepo;
import ru.social.animal.repository.TypeOfAnimalRepo;
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

    @Autowired
    private AdvertRepo advertRepo;

    @Autowired
    private CityRepo cityRepo;

    @Autowired
    private RegionRepo regionRepo;

    @Autowired
    private TypeOfAnimalRepo typeOfAnimalRepo;

    @GetMapping("/{id}")
    public String getAdvertById(@PathVariable Long id, Model model) {
        Advert advert = advertService.getAdvertById(id);
        model.addAttribute("advert", advert);
        return "advert-details";
    }

    @GetMapping("/create")
    public String showCreateAdvertPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
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
            Model model,
            Principal principal
    ) throws IOException {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        String relativePath = null;

        if (noImage == null && imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();

            // Проверка, есть ли уже файл с таким именем у пользователя
            if (advertRepo.existsByUserAndImageFileName(user, fileName)) {
                model.addAttribute("errorMessage", "У вас уже есть объявление с таким изображением: " + fileName);
                model.addAttribute("cities", cityRepo.findAll());
                model.addAttribute("regions", regionRepo.findAll());
                model.addAttribute("animalTypes", typeOfAnimalRepo.findAll());
                return "create";
            }

            Path workingDir = Paths.get("").toAbsolutePath();
            File uploadDir = new File(workingDir.toFile(), "uploads/" + user.getId());
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + uploadDir.getAbsolutePath());
            }

            File destinationFile = new File(uploadDir, fileName);
            imageFile.transferTo(destinationFile);
            relativePath = "/uploads/" + user.getId() + "/" + fileName;
        }

        advertService.createAdvert(description, address, relativePath, cityId, regionId, typeOfAnimalId, user);
        return "redirect:/tape";
    }


    // Список «Моих объявлений»
    @GetMapping("/my-adverts")
    public String myAdverts(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);

        //User user = userService.findByEmail(principal.getName()).orElseThrow();
        List<Advert> list = advertService.getMyAdverts(user);
        model.addAttribute("adverts", list);

        return "my-adverts";
    }

    // Форма редактирования одного объявления
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);

        Advert advert = advertService.getAdvertById(id);

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
            Model model,
            Principal principal
    ) throws IOException {
        Advert advert = advertService.getAdvertById(id);
        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        User user = userOpt.get();

        advert.setDescription(description);
        advert.setAddress(address);
        advert.setCity(cityService.findById(cityId).orElse(null));
        advert.setRegion(regionService.findById(regionId).orElse(null));
        advert.setTypeOfAnimal(typeOfAnimalService.findById(typeOfAnimalId).orElse(null));
        advert.setFound(Boolean.TRUE.equals(isFound));

        if (imageFile != null && !imageFile.isEmpty()) {
            // Проверка: уже есть файл с таким именем в другом объявлении пользователя?
            List<Advert> userAdverts = advertService.getAdvertsByUser(user);
            for (Advert otherAdvert : userAdverts) {
                if (!otherAdvert.getId().equals(id) &&
                        otherAdvert.getLinkImage() != null &&
                        otherAdvert.getLinkImage().endsWith("/" + imageFile.getOriginalFilename())) {
                    model.addAttribute("advert", advert);
                    model.addAttribute("cities", cityService.findAll());
                    model.addAttribute("regions", regionService.findAll());
                    model.addAttribute("animalTypes", typeOfAnimalService.findAll());
                    model.addAttribute("imageError", "У вас уже есть объявление с такой фотографией.");
                    return "edit-advert";
                }
            }

            // Сохраняем новый файл
            Path workingDir = Paths.get("").toAbsolutePath();
            File uploadDir = new File(workingDir.toFile(), "uploads/" + user.getId());
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + uploadDir.getAbsolutePath());
            }

            File destinationFile = new File(uploadDir, imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);

            String relativePath = "/uploads/" + user.getId() + "/" + imageFile.getOriginalFilename();
            advert.setLinkImage(relativePath);
        }

        advertService.save(advert);
        return "redirect:/adverts/my-adverts";
    }

    @GetMapping("/profile-view")
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("user", userOpt.get());
        return "profile-view";
    }



}

