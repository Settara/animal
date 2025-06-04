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
import java.util.Comparator;
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

        List<City> cities = cityService.findAll();
        cities.sort(Comparator.comparing(City::getTitle)); // Сортировка

        model.addAttribute("cities", cities);
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
            @RequestParam("typeOfAnimalId") Long typeOfAnimalId,
            @RequestParam(value = "giveAway", defaultValue = "false") boolean giveAway,
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

            // Проверка: есть ли у пользователя объявление с таким изображением
            List<Advert> existingAdverts = advertService.getAdvertsByUser(user);
            for (Advert advert : existingAdverts) {
                String imagePath = advert.getLinkImage();
                if (imagePath != null && imagePath.endsWith("/" + fileName)) {
                    model.addAttribute("errorMessage", "Вы уже создавали объявление с этим животным. Пожалуйста, загрузите другое фото.");
                    model.addAttribute("cities", cityRepo.findAll());
                    model.addAttribute("regions", regionRepo.findAll());
                    model.addAttribute("animalTypes", typeOfAnimalRepo.findAll());
                    model.addAttribute("user", user);
                    return "create";
                }
            }

            // сохраняем файл
            Path workingDir = Paths.get("").toAbsolutePath();
            File uploadDir = new File(workingDir.toFile(), "uploads/" + user.getId());
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию: " + uploadDir.getAbsolutePath());
            }

            File destinationFile = new File(uploadDir, fileName);
            imageFile.transferTo(destinationFile);
            relativePath = "/uploads/" + user.getId() + "/" + fileName;
        }


        advertService.createAdvert(description, address, relativePath, cityId, typeOfAnimalId, giveAway, user);
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

        List<City> cities = cityService.findAll();
        cities.sort(Comparator.comparing(City::getTitle)); // Сортировка

        model.addAttribute("cities", cities);
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());
        return "edit-advert";
    }

    @PostMapping("/{id}/edit")
    public String updateAdvert(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam String address,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam Long cityId,
            @RequestParam Long typeOfAnimalId,
            @RequestParam(value = "isFound", required = false) Boolean isFound,
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

        City city = cityService.findById(cityId).orElse(null);
        advert.setCity(city);

        if (city != null) {
            advert.setRegion(city.getRegion()); // <-- автоматическое определение региона
        }

        advert.setTypeOfAnimal(typeOfAnimalService.findById(typeOfAnimalId).orElse(null));
        advert.setFound(Boolean.TRUE.equals(isFound));

        if (imageFile != null && !imageFile.isEmpty()) {
            List<Advert> userAdverts = advertService.getAdvertsByUser(user);
            for (Advert otherAdvert : userAdverts) {
                if (!otherAdvert.getId().equals(id) &&
                        otherAdvert.getLinkImage() != null &&
                        otherAdvert.getLinkImage().endsWith("/" + imageFile.getOriginalFilename())) {
                    model.addAttribute("advert", advert);
                    model.addAttribute("cities", cityService.findAll());
                    model.addAttribute("animalTypes", typeOfAnimalService.findAll());
                    model.addAttribute("imageError", "У вас уже есть объявление с такой фотографией.");
                    return "edit-advert";
                }
            }

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

    @GetMapping("/giveaway")
    public String giveawayAdverts(
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "regionId", required = false) Long regionId,
            @RequestParam(value = "typeOfAnimalId", required = false) Long typeOfAnimalId,
            Model model,
            Principal principal
    ) {
        if (principal != null) {
            userService.findByEmail(principal.getName()).ifPresent(user -> model.addAttribute("user", user));
        }

        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("animalTypes", typeOfAnimalService.findAll());

        List<Advert> adverts = advertService.getFilteredGiveAwayAdverts(cityId, regionId, typeOfAnimalId);
        model.addAttribute("adverts", adverts);

        return "giveaway";
    }

}

