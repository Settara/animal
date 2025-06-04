package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.City;
import java.util.List;

public interface CityRepo extends JpaRepository<City, Long> {
    List<City> findByRegionId(Long regionId);
    List<City> findByTitleStartingWithIgnoreCase(String title);

}
