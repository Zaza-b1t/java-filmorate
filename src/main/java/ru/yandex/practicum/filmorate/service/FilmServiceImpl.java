package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
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
        log.info("Добавление фильма: name={}", film.getName());
        filmStorage.addFilm(film);
        log.debug("Фильм добавлен: id={}, name={}", film.getId(), film.getName());
    }

    @Override
    public void removeFilm(Long filmId) {
        log.info("Удаление фильма: id={}", filmId);
        requireFilm(filmId);
        filmStorage.removeFilm(filmId);
        log.debug("Фильм удалён: id={}", filmId);
    }

    @Override
    public void updateFilm(Film film) {
        log.info("Обновление фильма: id={}", film.getId());
        if (film.getId() == null) {
            throw new ValidationException("ID фильма обязателен для обновления.");
        }
        requireFilm(film.getId());
        filmStorage.updateFilm(film);
        log.debug("Фильм обновлён: id={}", film.getId());
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Получение фильма: id={}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        Collection<Film> films = filmStorage.getAllFilms();
        log.debug("Кол-во фильмов: {}", films.size());
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка: filmId={}, userId={}", filmId, userId);
        Film film = requireFilm(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(film.getId(), userId);
        log.debug("Лайк добавлен: filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка: filmId={}, userId={}", filmId, userId);
        Film film = requireFilm(filmId);
        userService.getUserById(userId);
        filmStorage.deleteLike(film.getId(), userId);
        log.debug("Лайк удалён: filmId={}, userId={}", filmId, userId);
    }

    @Override
    public List<Film> listOfFilms(int count) {
        log.info("Запрос популярных фильмов: limit={}", count);
        List<Film> popular = filmStorage.getPopular(count).stream().toList();
        log.debug("Выдано популярных фильмов: {}", popular.size());
        return popular;
    }

    private Film requireFilm(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new EntityNotFoundException("Фильм с ID = " + id + " не найден.");
        }
        return film;
    }
}
