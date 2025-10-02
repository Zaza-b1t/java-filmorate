package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }

    @Override
    public void removeFilm(Long filmId) {
        // бросит 404, если нет
        filmStorage.getFilmById(filmId);
        filmStorage.removeFilm(filmId);
    }

    @Override
    public void updateFilm(Film film) {
        // бросит 404, если нет
        filmStorage.getFilmById(film.getId());
        filmStorage.updateFilm(film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> listOfFilms(int count) {
        return filmStorage.getPopular(count).stream().toList();
    }
}
