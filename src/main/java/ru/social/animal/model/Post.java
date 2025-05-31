package ru.social.animal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 1500)
    private String description;

    private String linkImageFirst;

    private String linkImageSecond;

    private String linkImageThird;

    @NotNull
    private LocalDate datePublish;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
