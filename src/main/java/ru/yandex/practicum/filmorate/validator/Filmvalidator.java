package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class Filmvalidator implements Validator<Film> {
    @Override
    public void validate(Film film) {
        checkReleaseDate(film.getReleaseDate());
    }


    public void validate(Film film, Map<Long,Film> films) {
        validate(film);
        checkIdNotNull(film.getId());
        checkExists(film.getId(), films);
    }

    private void checkReleaseDate(LocalDate date) {
        if (date.isBefore(LocalDate.of(1895,12,28))) {
            log.error("Ошибка валидации: дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    private void checkIdNotNull(Long id) {
        if (id == null) {
            log.error("Ошибка валидации: ID не может быть null");
            throw new ValidationException("ID не может быть null");
        }
    }

    private void checkExists(Long id, Map<Long, Film> films) {
        if (!films.containsKey(id)) {
            log.error("Ошибка: фильм с таким ID не найден");
            throw new EntityNotFoundException("Фильм с таким ID не найден");
        }
    }
}
