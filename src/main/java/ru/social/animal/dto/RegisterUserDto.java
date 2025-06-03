package ru.social.animal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String secondName;

    @NotBlank
    @Pattern(regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$", message = "Формат номера: +7 (XXX) XXX-XX-XX")
    private String phone;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    @NotBlank
    private String sex;
}
