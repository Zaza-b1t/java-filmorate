package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long,User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
    }

    @Override
    public void removeUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Пользователь с ID = " + user.getId() + " не найден.");
        }
        users.put(user.getId(), user);
    }


    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if(user == null) {
            return false;
        }
        return user.getFriends().contains(friendId);
    }

    @Override
    public Collection<User> getUsersByIds(Collection<Long> userIds) {
        return userIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
