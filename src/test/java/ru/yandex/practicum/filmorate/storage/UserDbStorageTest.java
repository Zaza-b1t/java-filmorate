package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testAddAndFindUser() {
        User user = new User(null, "test@mail.com", "login", "Test User", LocalDate.now());
        userStorage.addUser(user);

        User found = userStorage.getUserById(user.getId());

        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", "test@mail.com")
                .hasFieldOrPropertyWithValue("login", "login");
    }
}
