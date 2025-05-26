package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.TypeOfAnimal;
import ru.social.animal.repository.TypeOfAnimalRepo;

import java.util.List;
import java.util.Optional;

@Service
public class TypeOfAnimalService {

    @Autowired
    private TypeOfAnimalRepo typeOfAnimalRepo;

    public List<TypeOfAnimal> findAll() {
        return typeOfAnimalRepo.findAll();
    }

    public Optional<TypeOfAnimal> findById(Long id) {
        return typeOfAnimalRepo.findById(id);
    }
}
