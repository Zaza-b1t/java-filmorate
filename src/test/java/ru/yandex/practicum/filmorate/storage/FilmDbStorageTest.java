package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void testAddAndFindFilm() {
        Film film = new Film(
                null,
                "Test Film",
                "Description",
                LocalDate.of(2020, 1, 1),
                120,
                new Mpa(1, "G"),
                java.util.List.of()
        );

        filmStorage.addFilm(film);

        Film found = filmStorage.getFilmById(film.getId());

        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("duration", 120)
                .extracting(f -> f.getMpa().getId())
                .isEqualTo(1);
    }
}
