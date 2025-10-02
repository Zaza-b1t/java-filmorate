package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();


    @Override
    public void addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(),film);
    }

    @Override
    public void removeFilm(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).remove(userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return films.values().stream()
                .sorted((a,b) -> Integer.compare(
                        likes.getOrDefault(b.getId(), Set.of()).size(),
                        likes.getOrDefault(a.getId(), Set.of()).size()
                ))
                .limit(count)
                .toList();
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
