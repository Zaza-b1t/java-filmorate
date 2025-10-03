package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Genre> getAll() {
        log.info("Получение списка всех жанров");
        Collection<Genre> genres = genreStorage.getAll();
        log.debug("Кол-во жанров: {}", genres.size());
        return genres;
    }

    @Override
    public Genre getById(Integer id) {
        log.info("Получение жанра: id={}", id);
        Genre genre = genreStorage.getById(id);
        log.debug("Жанр найден: id={}, name={}", genre.getId(), genre.getName());
        return genre;
    }
}
