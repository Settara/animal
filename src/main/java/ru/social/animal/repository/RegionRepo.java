package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.Region;

public interface RegionRepo extends JpaRepository<Region, Long> {
}
