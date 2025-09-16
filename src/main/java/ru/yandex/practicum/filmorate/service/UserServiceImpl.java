package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
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
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с таким ID не найден.");
        }
        return user;
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

        User user   = getUserById(userId);
        User friend = getUserById(friendId);

        if (!userStorage.isFriend(userId,friendId)) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        }
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user   = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            return;
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = getUserById(userId);
        Set<Long> friendIds = user.getFriends();
        Collection<User> friends = userStorage.getUsersByIds(friendIds);
        return new HashSet<>(friends);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        User u1 = getUserById(userId);
        User u2 = getUserById(friendId);

        Set<Long> commonIds = new HashSet<>(u1.getFriends());
        commonIds.retainAll(u2.getFriends());

        Collection<User> commonUsers = userStorage.getUsersByIds(commonIds);

        return new HashSet<>(commonUsers);
    }

    private void ensureUserExists(Long id) {
        if (id == null || userStorage.getUserById(id) == null) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
    }
}
