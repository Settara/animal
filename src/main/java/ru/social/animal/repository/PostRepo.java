package ru.social.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.social.animal.model.Post;
import ru.social.animal.model.User;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findAllByUserOrderByDatePublishDesc(User user);
}
