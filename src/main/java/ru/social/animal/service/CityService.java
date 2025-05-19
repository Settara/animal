package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.City;
import ru.social.animal.repository.CityRepo;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepo cityRepo;

    public List<City> findAll() {
        return cityRepo.findAll();
    }
}
