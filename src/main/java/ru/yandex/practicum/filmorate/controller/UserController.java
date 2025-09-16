package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.Uservalidator;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final Uservalidator userValidator;

    public UserController(UserService userService, Uservalidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя с логином: {}", user.getLogin());
        userValidator.validate(user);
        userService.addUser(user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя с ID = {}", user.getId());
        userValidator.validate(user);
        userService.updateUser(user);
        log.info("Пользователь с ID = {} успешно обновлен", user.getId());
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Добавление друга с ID = {} для пользователя с ID = {}", friendId, userId);
        userService.addFriend(userId, friendId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Удаление друга с ID = {} для пользователя с ID = {}", friendId, userId);
        userService.removeFriend(userId, friendId);
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable Long userId) {
        log.info("Получение списка друзей пользователя с ID = {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public  Collection<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        log.info("Получение общих друзей пользователя {} и {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }
}

