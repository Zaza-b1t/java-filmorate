package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(User user);

    void removeUser(Long userId);

    void updateUser(User user);

    User getUserById(Long id);

    Collection<User> getAllUsers();

    boolean isFriend(Long userId, Long friendId);

    Collection<User> getUsersByIds(Collection<Long> userIds);
}
