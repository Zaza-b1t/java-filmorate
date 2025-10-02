package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addUser(User user) {
        userStorage.addUser(user);
    }

    @Override
    public void removeUser(Long userId) {
        ensureUserExists(userId);
        userStorage.removeUser(userId);
    }

    @Override
    public void updateUser(User user) {
        ensureUserExists(user.getId());
        userStorage.updateUser(user);
    }

    @Override
    public User getUserById(Long id) {
        var u = userStorage.getUserById(id);
        if (u == null) throw new EntityNotFoundException("Пользователь с таким ID не найден.");
        return u;
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя.");
        }
        getUserById(userId);
        getUserById(friendId);

        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        return userStorage.getCommonFriends(userId, friendId);
    }

    private void ensureUserExists(Long id) {
        if (id == null || userStorage.getUserById(id) == null) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
    }
}
