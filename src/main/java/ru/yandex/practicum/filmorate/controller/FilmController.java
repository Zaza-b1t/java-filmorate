package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Filmvalidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code POST /films} - Создание фильма<br/>
 * {@code GET /films} - получение всех фильмов<br/>
 * {@code PUT /films} - обновление фильма<br/>
 */

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    Filmvalidator filmvalidator = new Filmvalidator();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма: {}",film);
        filmvalidator.validate(film);
        film.setId(getNextId());
        films.put(film.getId(),film);
        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм с ID = {}", film.getId());
        filmvalidator.validate(film, films);
        films.put(film.getId(), film);
        log.info("Фильм с ID {} обновлен", film.getId());
        return film;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
