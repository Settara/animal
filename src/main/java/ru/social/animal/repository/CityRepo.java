package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.City;

public interface CityRepo extends JpaRepository<City, Long> {
}
