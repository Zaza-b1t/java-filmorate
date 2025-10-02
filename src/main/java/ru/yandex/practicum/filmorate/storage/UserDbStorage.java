package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private static final String FIND_ALL_QUERY   = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY     =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY     = "DELETE FROM users WHERE id = ?";
    private static final String MERGE_FRIEND_SQL =
            "MERGE INTO friendship (user_id, friend_id) KEY (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_SQL =
            "DELETE FROM friendship WHERE user_id=? AND friend_id=?";
    private static final String FRIENDS_OF_SQL =
            "SELECT u.* FROM users u " +
                    "JOIN friendship f ON f.friend_id = u.id " +
                    "WHERE f.user_id=?";
    private static final String COMMON_FRIENDS_SQL =
            "SELECT u.* FROM users u WHERE u.id IN (" +
                    "  SELECT f1.friend_id FROM friendship f1 " +
                    "  JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                    "  WHERE f1.user_id=? AND f2.user_id=?" +
                    ")";


    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;

    public UserDbStorage(JdbcTemplate jdbc,
                         UserRowMapper userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbc.query(FIND_ALL_QUERY, userRowMapper);
    }

    @Override
    public User getUserById(Long id) {
        var users = jdbc.query(FIND_BY_ID_QUERY, userRowMapper, id);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        return users.get(0);
    }

    @Override
    public void addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
        }
    }

    @Override
    public void updateUser(User user) {
        int updated = jdbc.update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId()
        );
        if (updated == 0) {
            throw new EntityNotFoundException("Пользователь с ID = " + user.getId() + " не найден.");
        }
    }

    @Override
    public void removeUser(Long userId) {
        int deleted = jdbc.update(DELETE_QUERY, userId);
        if (deleted == 0) {
            throw new EntityNotFoundException("Пользователь с ID = " + userId + " не найден.");
        }
    }

    @Override
    public Collection<User> getUsersByIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("?");
        }
        String placeholders = sb.toString();
        String sql = "SELECT * FROM users WHERE id IN (" + placeholders + ")";
        return jdbc.query(sql, userRowMapper, userIds.toArray());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(MERGE_FRIEND_SQL, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(DELETE_FRIEND_SQL, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return jdbc.query(FRIENDS_OF_SQL, userRowMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        return jdbc.query(COMMON_FRIENDS_SQL, userRowMapper, userId, otherUserId);
    }

}
