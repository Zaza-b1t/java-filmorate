package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private static final String FIND_ALL_SQL = "SELECT id, name FROM mpa ORDER BY id";
    private static final String FIND_BY_ID_SQL = "SELECT id, name FROM mpa WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Mpa> mapper;

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<Mpa> getAll() {
        return jdbc.query(FIND_ALL_SQL, mapper);
    }

    @Override
    public Mpa getById(Integer id) {
        var list = jdbc.query(FIND_BY_ID_SQL, mapper, id);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("Рейтинг MPA id=" + id + " не найден");
        }
        return list.get(0);
    }
}
