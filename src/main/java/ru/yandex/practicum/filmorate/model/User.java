package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
<<<<<<< HEAD
=======

    private Set<Long> friends = new HashSet<>();

    private Set<Friendship> friendships = new HashSet<>();
>>>>>>> 4a82a1a (feat(fz11): реализованы лайки и дружба без БД)
}
