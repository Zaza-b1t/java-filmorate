package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }

    @Override
    public void removeFilm(Long filmId) {
        ensureFilmExists(filmId);
        filmStorage.removeFilm(filmId);
    }

    @Override
    public void updateFilm(Film film) {
        ensureFilmExists(film.getId());
        filmStorage.updateFilm(film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Фильм с таким ID не найден.");
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);

        film.getLikedByUsers().add(userId);
        film.setLikeCount(film.getLikedByUsers().size());
        filmStorage.updateFilm(film);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);

        film.getLikedByUsers().remove(userId);
        film.setLikeCount(film.getLikedByUsers().size());
        filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> listOfFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void ensureFilmExists(Long id) {
        if (id == null || filmStorage.getFilmById(id) == null) {
            throw new EntityNotFoundException("Фильм с ID = " + id + " не найден.");
        }
    }
}
