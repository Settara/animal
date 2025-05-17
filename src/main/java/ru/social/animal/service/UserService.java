package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.dto.RegisterUserDto;
import ru.social.animal.model.User;
import ru.social.animal.repository.UserRepo;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public boolean userExistsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public User registerNewUser(RegisterUserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        user.setPassword(dto.getPassword());

        return userRepo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }
}
