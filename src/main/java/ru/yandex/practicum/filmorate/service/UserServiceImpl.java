package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addUser(User user) {
        log.info("Добавление пользователя: login={}", user.getLogin());
        userStorage.addUser(user);
        log.debug("Пользователь добавлен: id={}, login={}", user.getId(), user.getLogin());
    }

    @Override
    public void removeUser(Long userId) {
        log.info("Удаление пользователя: id={}", userId);
        ensureUserExists(userId);
        userStorage.removeUser(userId);
        log.debug("Пользователь удалён: id={}", userId);
    }

    @Override
    public void updateUser(User user) {
        log.info("Обновление пользователя: id={}", user.getId());
        ensureUserExists(user.getId());
        userStorage.updateUser(user);
        log.debug("Пользователь обновлён: id={}", user.getId());
    }

    @Override
    public User getUserById(Long id) {
        log.info("Получение пользователя: id={}", id);
        User u = userStorage.getUserById(id);
        if (u == null) {
            throw new EntityNotFoundException("Пользователь с таким ID не найден.");
        }
        log.debug("Пользователь найден: id={}, login={}", u.getId(), u.getLogin());
        return u;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        Collection<User> users = userStorage.getAllUsers();
        log.debug("Кол-во пользователей: {}", users.size());
        return users;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья: userId={}, friendId={}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя.");
        }
        getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        log.debug("Друг добавлен: userId={}, friendId={}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей: userId={}, friendId={}", userId, friendId);
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
        log.debug("Друг удалён: userId={}, friendId={}", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        log.info("Получение друзей пользователя: userId={}", userId);
        getUserById(userId);
        Collection<User> friends = userStorage.getFriends(userId);
        log.debug("Кол-во друзей: userId={}, count={}", userId, friends.size());
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        log.info("Получение общих друзей: userId={}, otherId={}", userId, friendId);
        getUserById(userId);
        getUserById(friendId);
        Collection<User> common = userStorage.getCommonFriends(userId, friendId);
        log.debug("Кол-во общих друзей: userId={}, otherId={}, count={}", userId, friendId, common.size());
        return common;
    }

    private void ensureUserExists(Long id) {
        if (id == null || userStorage.getUserById(id) == null) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
    }
}
