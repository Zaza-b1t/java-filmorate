package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void addFilm(Film film);
    void removeFilm(Long filmId);
    void updateFilm(Film film);
    Film getFilmById(Long filmId);
    Collection<Film> getAllFilms();
}
