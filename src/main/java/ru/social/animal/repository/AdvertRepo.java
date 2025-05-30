package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.social.animal.model.Advert;
import ru.social.animal.model.User;

import java.time.LocalDate;
import java.util.List;

public interface AdvertRepo extends JpaRepository<Advert, Long> {

    List<Advert> findAllByOrderByDatePublishDesc();

    @Query("SELECT a FROM Advert a WHERE " +
            "(:city_id IS NULL OR a.city.id = :city_id) AND " +
            "(:region_id IS NULL OR a.region.id = :region_id) AND " +
            "(:typeOfAnimal_id IS NULL OR a.typeOfAnimal.id = :typeOfAnimal_id)" +
            "ORDER BY a.datePublish DESC")
    List<Advert> findByFilters(@Param("city_id") Long city_id,
                               @Param("region_id") Long region_id,
                               @Param("typeOfAnimal_id") Long typeOfAnimal_id);

    List<Advert> findAllByUserOrderByDatePublishDesc(User user);

    @Query("SELECT COUNT(a) > 0 FROM Advert a WHERE a.user = :user AND a.linkImage LIKE %:fileName")
    boolean existsByUserAndImageFileName(@Param("user") User user, @Param("fileName") String fileName);

}
