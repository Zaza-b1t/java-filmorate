package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;

    @Positive
    private int duration;
    private int likeCount;
    private Set<Long> likedByUsers = new HashSet<>();
    Set<String> genres;
    String mpa;
}