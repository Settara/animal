package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.Post;
import ru.social.animal.model.User;
import ru.social.animal.repository.PostRepo;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepo postRepo;

    public List<Post> findAllByUser(User user) {
        return postRepo.findAllByUserOrderByDatePublishDesc(user);
    }

    public Post save(Post post) {
        return postRepo.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepo.findById(id);
    }

    public Long findUserIdByPostId(Long postId) {
        return postRepo.findById(postId)
                .map(post -> post.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
    }
}
