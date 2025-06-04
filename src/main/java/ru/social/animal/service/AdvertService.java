package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.Advert;
import ru.social.animal.model.City;
import ru.social.animal.model.User;
import ru.social.animal.repository.AdvertRepo;
import ru.social.animal.repository.CityRepo;
import ru.social.animal.repository.RegionRepo;
import ru.social.animal.repository.TypeOfAnimalRepo;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdvertService {

    @Autowired
    private AdvertRepo advertRepo;

    @Autowired
    private CityRepo cityRepo;

    @Autowired
    private RegionRepo regionRepo;

    @Autowired
    private TypeOfAnimalRepo typeOfAnimalRepo;

    public void createAdvert(String description, String address, String linkImage,
                             Long cityId, Long typeOfAnimalId,
                             boolean giveAway, User user) {

        Advert advert = new Advert();
        advert.setDescription(description);
        advert.setAddress(address);
        advert.setLinkImage(linkImage);
        advert.setFound(false);
        advert.setGiveAway(giveAway); // <-- добавлено
        advert.setDatePublish(LocalDate.now());
        advert.setUser(user);
        advert.setCity(cityRepo.findById(cityId).orElse(null));

        // Автоматически определяем регион из города
        City city = cityRepo.findById(cityId).orElseThrow();
        advert.setRegion(city.getRegion());

        //advert.setRegion(regionRepo.findById(regionId).orElse(null));
        advert.setTypeOfAnimal(typeOfAnimalRepo.findById(typeOfAnimalId).orElse(null));

        advertRepo.save(advert);
    }


    public void save(Advert advert) {
        advertRepo.save(advert);
    }

    public List<Advert> getFilteredAdverts(Long cityId, Long regionId, Long typeOfAnimalId) {
        if (cityId == null && regionId == null && typeOfAnimalId == null) {
            return advertRepo.findAllByOrderByDatePublishDesc(); // все по убыванию даты
        }

        return advertRepo.findByFilters(cityId, regionId, typeOfAnimalId);
    }

    public Advert getAdvertById(Long id) {
        return advertRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с id " + id + " не найдено"));
    }

    public List<Advert> getMyAdverts(User user) {
        return advertRepo.findAllByUserOrderByDatePublishDesc(user);
    }

    public List<Advert> getAdvertsByUser(User user) {
        return advertRepo.findAllByUserOrderByDatePublishDesc(user);
    }

    public void markAsFound(Long advertId) {
        Advert a = getAdvertById(advertId);
        a.setFound(true);
        advertRepo.save(a);
    }

    public List<Advert> getFilteredGiveAwayAdverts(Long cityId, Long regionId, Long typeOfAnimalId) {
        if (cityId == null && regionId == null && typeOfAnimalId == null) {
            return advertRepo.findAllByOrderByDatePublishDesc(); // все по убыванию даты
        }
        return advertRepo.findGiveAwayAdvertsWithFilters(cityId, regionId, typeOfAnimalId);
    }

}
