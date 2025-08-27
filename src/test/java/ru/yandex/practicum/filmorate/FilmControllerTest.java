package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Filmvalidator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
	Filmvalidator filmvalidator = new Filmvalidator();

	@Test
	void shouldValidateCorrectFilm() {
		Film film = new Film(1L, "Титаник", "Описание",
				LocalDate.of(2000, 1, 1), 120);
		assertDoesNotThrow(() -> filmvalidator.validate(film));
	}

	@Test
	void shouldFailIfReleaseDateTooEarly() {
		Film film = new Film(null, "Название", "Описание",
				LocalDate.of(1800, 1, 1), 90);

		assertThrows(ValidationException.class, () -> filmvalidator.validate(film));
	}

	@Test
	void shouldFailIfIdNullInUpdate() {
		Film film = new Film(null, "Название", "Описание",
				LocalDate.of(2000, 1, 1), 90);

		assertThrows(ValidationException.class,
				() -> filmvalidator.validate(film, new HashMap<>()));
	}

	@Test
	void shouldFailIfIdNotExistsInUpdate() {
		Film film = new Film(99L, "Название", "Описание",
				LocalDate.of(2000, 1, 1), 90);

		Map<Long, Film> films = new HashMap<>();
		films.put(1L, film);

		assertThrows(EntityNotFoundException.class,
				() -> filmvalidator.validate(film, films));
	}


}
