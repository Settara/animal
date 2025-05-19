package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    //Тут использовать аннотацию @Query
    Optional<User> findByEmail(String email);
}
