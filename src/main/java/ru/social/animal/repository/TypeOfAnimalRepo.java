package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.TypeOfAnimal;

public interface TypeOfAnimalRepo extends JpaRepository<TypeOfAnimal, Long> {
}
