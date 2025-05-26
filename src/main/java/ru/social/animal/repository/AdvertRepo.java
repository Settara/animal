package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.social.animal.model.Advert;

import java.time.LocalDate;
import java.util.List;

public interface AdvertRepo extends JpaRepository<Advert, Long> {

    List<Advert> findAllByOrderByDatePublishDesc();

    @Query("SELECT a FROM Advert a WHERE " +
            "(:cityId IS NULL OR a.city.id = :cityId) AND " +
            "(:regionId IS NULL OR a.region.id = :regionId)" +
            "ORDER BY a.datePublish DESC")
    List<Advert> findByFilters(@Param("cityId") Long cityId,
                               @Param("regionId") Long regionId);
}
