package ru.social.animal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.social.animal.model.Post;
import ru.social.animal.model.User;
import ru.social.animal.service.PostService;
import ru.social.animal.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String viewPost(@PathVariable("id") Long postId, Model model) {
        Post post = postService.findById(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        User user = post.getUser();

        model.addAttribute("post", post);
        model.addAttribute("user", user);

        return "post-view";
    }

    @GetMapping("/create")
    public String showCreatePostPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "create-post";
    }

    @PostMapping("/add")
    public String createPost(@RequestParam("description") String description,
                             @RequestParam("image1") MultipartFile image1,
                             @RequestParam("image2") MultipartFile image2,
                             @RequestParam("image3") MultipartFile image3,
                             Principal principal,
                             RedirectAttributes redirectAttributes) throws IOException {

        User user = userService.findByEmail(principal.getName()).orElseThrow();

        Post post = new Post();
        post.setDescription(description);
        post.setDatePublish(LocalDate.now());
        post.setUser(user);

        post = postService.save(post);

        Path workingDir = Paths.get("").toAbsolutePath();
        File postDir = new File(workingDir.toFile(), "user-posts/" + user.getEmail() + "/" + post.getId());
        if (!postDir.exists() && !postDir.mkdirs()) {
            throw new IOException("Не удалось создать директорию: " + postDir.getAbsolutePath());
        }

        if (image1 != null && !image1.isEmpty()) {
            String fileName = image1.getOriginalFilename();
            File dest = new File(postDir, fileName);
            image1.transferTo(dest);
            post.setLinkImageFirst("/user-posts/" + user.getEmail() + "/" + post.getId() + "/" + fileName);
        }

        if (image2 != null && !image2.isEmpty()) {
            String fileName = image2.getOriginalFilename();
            File dest = new File(postDir, fileName);
            image2.transferTo(dest);
            post.setLinkImageSecond("/user-posts/" + user.getEmail() + "/" + post.getId() + "/" + fileName);
        }

        if (image3 != null && !image3.isEmpty()) {
            String fileName = image3.getOriginalFilename();
            File dest = new File(postDir, fileName);
            image3.transferTo(dest);
            post.setLinkImageThird("/user-posts/" + user.getEmail() + "/" + post.getId() + "/" + fileName);
        }

        postService.save(post);

        redirectAttributes.addFlashAttribute("success", "Пост успешно создан!");
        return "redirect:/profile/" + user.getId();
    }



}

