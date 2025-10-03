package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    public MpaServiceImpl(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Mpa> getAll() {
        log.info("Получение списка всех MPA рейтингов");
        Collection<Mpa> mpa = mpaStorage.getAll();
        log.debug("Кол-во MPA рейтингов: {}", mpa.size());
        return mpa;
    }

    @Override
    public Mpa getById(int id) {
        log.info("Получение MPA рейтинга: id={}", id);
        Mpa mpa = mpaStorage.getById(id);
        log.debug("MPA рейтинг найден: id={}, name={}", mpa.getId(), mpa.getName());
        return mpa;
    }
}
