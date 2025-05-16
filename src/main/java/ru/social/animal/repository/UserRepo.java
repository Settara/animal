package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.social.animal.model.User;

@Repository
public interface UserRepo extends JpaRepository <User, Long> {
}
