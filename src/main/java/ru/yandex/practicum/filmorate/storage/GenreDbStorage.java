package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {

    private static final String FIND_ALL = "SELECT id, name FROM genres ORDER BY id";
    private static final String FIND_ONE = "SELECT id, name FROM genres WHERE id=?";

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbc.query(FIND_ALL, mapper);
    }

    @Override
    public Genre getById(Integer id) {
        List<Genre> list = jdbc.query(FIND_ONE, mapper, id);
        if (list.isEmpty()) throw new EntityNotFoundException("Жанр id=" + id + " не найден");
        return list.get(0);
    }
}
