package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(User user);

    void removeUser(Long userId);

    void updateUser(User user);

    User getUserById(Long id);

    Collection<User> getAllUsers();

    Collection<User> getUsersByIds(Collection<Long> userIds);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);

}
