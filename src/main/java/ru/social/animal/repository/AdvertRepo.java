package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.Advert;

public interface AdvertRepo extends JpaRepository<Advert, Long> {
}
