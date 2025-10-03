package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.nCopies;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String BASE_SELECT =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "       m.id AS mpa_id, m.name AS mpa_name " +
                    "FROM films f JOIN mpa m ON m.id = f.mpa_id ";
    private static final String FIND_ALL_QUERY   = BASE_SELECT;
    private static final String FIND_BY_ID_QUERY = BASE_SELECT + "WHERE f.id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                    "mpa_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String ADD_LIKE_SQL =
            "MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_SQL =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String POPULAR_SQL =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "       m.id AS mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON m.id = f.mpa_id " +
                    "LEFT JOIN (SELECT film_id, COUNT(*) AS like_count FROM likes GROUP BY film_id) lc " +
                    "  ON lc.film_id = f.id " +
                    "ORDER BY COALESCE(lc.like_count, 0) DESC, f.id ASC " +
                    "LIMIT ?";

    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id=?";
    private static final String INSERT_FILM_GENRE =
            "MERGE INTO film_genres (film_id, genre_id) KEY (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_GENRES_BY_FILM =
            "SELECT g.id, g.name FROM genres g " +
                    "JOIN film_genres fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id=?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public void addFilm(Film film) {
        KeyHolder kh = new GeneratedKeyHolder();

        ensureMpaExists(film.getMpa().getId());
        ensureGenresExist(film.getGenres());

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);

        var key = kh.getKey();
        if (key != null) {
            film.setId(key.longValue());
            saveGenres(film);
        }
    }

    @Override
    public void removeFilm(Long filmId) {
        int deleted = jdbc.update(DELETE_QUERY, filmId);
        if (deleted == 0) {
            throw new EntityNotFoundException("Фильм с ID = " + filmId + " не найден.");
        }
        jdbc.update(DELETE_FILM_GENRES, filmId);
    }

    @Override
    public void updateFilm(Film film) {
        ensureMpaExists(film.getMpa().getId());
        ensureGenresExist(film.getGenres());

        int updated = jdbc.update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (updated == 0) {
            throw new EntityNotFoundException("Фильм с ID = " + film.getId() + " не найден.");
        }
        saveGenres(film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        var films = jdbc.query(FIND_BY_ID_QUERY, filmRowMapper, filmId);
        if (films.isEmpty()) {
            throw new EntityNotFoundException("Фильм с ID = " + filmId + " не найден.");
        }
        Film film = films.get(0);
        film.setGenres(loadGenres(film.getId()));
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> result = jdbc.query(FIND_ALL_QUERY, filmRowMapper);
        for (Film f : result) {
            f.setGenres(loadGenres(f.getId()));
        }
        return result;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_SQL, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE_SQL, filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        List<Film> result = jdbc.query(POPULAR_SQL, filmRowMapper, count);
        for (Film f : result) {
            f.setGenres(loadGenres(f.getId()));
        }
        return result;
    }

    private void saveGenres(Film film) {
        jdbc.update(DELETE_FILM_GENRES, film.getId());
        if (film.getGenres() == null) return;

        for (Genre genre : film.getGenres()) {
            jdbc.update(INSERT_FILM_GENRE, film.getId(), genre.getId());
        }
    }

    private List<Genre> loadGenres(Long filmId) {
        return jdbc.query(FIND_GENRES_BY_FILM,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId);
    }

    private void ensureMpaExists(int mpaId) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM mpa WHERE id=?", Integer.class, mpaId);
        if (cnt == null || cnt == 0) throw new EntityNotFoundException("Рейтинг MPA id=" + mpaId + " не найден");
    }


    private void ensureGenresExist(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        List<Integer> ids = genres.stream()
                .map(Genre::getId)
                .distinct()
                .toList();

        String placeholders = String.join(",", nCopies(ids.size(), "?"));
        String sql = "SELECT id FROM genres WHERE id IN (" + placeholders + ")";

        List<Integer> existing = jdbc.queryForList(sql, Integer.class, ids.toArray());

        if (existing.size() != ids.size()) {
            var existingSet = new java.util.HashSet<>(existing);
            List<Integer> missing = ids.stream()
                    .filter(i -> !existingSet.contains(i))
                    .toList();
            throw new EntityNotFoundException("Жанры не найдены: " + missing);
        }
    }
}
