package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Slf4j
public class Uservalidator implements Validator<User> {

    @Override
    public void validate(User user){
        checkUserName(user);
    }

    public void validate(User user, Map<Long,User> users) {
        validate(user);
        checkIdNotNull(user.getId());
        checkExists(user.getId(), users);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован логин");
        }
    }

    private void checkIdNotNull(Long id) {
        if (id == null) {
            log.error("Ошибка валидации: ID не может быть null");
            throw new ValidationException("ID не может быть null");
        }
    }

    private void checkExists(Long id, Map<Long, User> users) {
        if (!users.containsKey(id)) {
            log.error("Ошибка: пользователь с таким ID не найден");
            throw new EntityNotFoundException("Пользователь с таким ID не найден");
        }
    }
}
