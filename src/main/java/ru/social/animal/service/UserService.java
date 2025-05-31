package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.social.animal.dto.RegisterUserDto;
import ru.social.animal.model.User;
import ru.social.animal.repository.UserRepo;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public boolean userExistsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public boolean userExistsByPhone(String phone) {
        return userRepo.findByPhone(phone).isPresent();
    }

    public boolean userExistsByPhoneExcludingUserId(String phone, Long userId) {
        Optional<User> userOptional = userRepo.findByPhone(phone);
        return userOptional.isPresent() && !userOptional.get().getId().equals(userId);
    }

    public User registerNewUser(RegisterUserDto dto) {
        if (userExistsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Пользователь с таким номером телефона уже существует");
        }

        if (userExistsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с такой электронной почтой уже существует");
        }

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

    public User updateUser(User existingUser, RegisterUserDto dto) {
        if (userExistsByPhoneExcludingUserId(dto.getPhone(), existingUser.getId())) {
            throw new IllegalArgumentException("Номер телефона уже занят другим пользователем");
        }

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setSecondName(dto.getSecondName());
        existingUser.setPhone(dto.getPhone());
        existingUser.setSex(dto.getSex());

        return userRepo.save(existingUser);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }

    public User save(User user) {
        return userRepo.save(user);
    }
}
