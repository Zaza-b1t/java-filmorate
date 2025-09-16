package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    void addFilm(Film film);

    void removeFilm(Long filmId);

    void updateFilm(Film film);

    Film getFilmById(Long filmId);

    Collection<Film> getAllFilms();

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> listOfFilms(int count);
}
