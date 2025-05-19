package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.TypeOfAnimal;
import ru.social.animal.repository.TypeOfAnimalRepo;

import java.util.List;

@Service
public class TypeOfAnimalService {

    @Autowired
    private TypeOfAnimalRepo typeOfAnimalRepo;

    public List<TypeOfAnimal> findAll() {
        return typeOfAnimalRepo.findAll();
    }
}
