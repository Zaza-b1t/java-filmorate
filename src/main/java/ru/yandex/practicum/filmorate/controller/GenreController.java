package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping
    public Collection<Genre> all() {
        log.info("Запрос списка всех жанров");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Genre one(@PathVariable Integer id) {
        log.info("Запрос жанра по id={}", id);
        return service.getById(id);
    }
}
