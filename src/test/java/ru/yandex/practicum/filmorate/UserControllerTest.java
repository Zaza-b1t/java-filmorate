package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collection;

public class UserControllerTest {

    private final UserController userController = new UserController();

    @Test
    public void testAddUser() {
        User user = new User(null, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testlogin", user.getLogin());
        assertEquals("Test User", user.getName());
    }

    @Test
    public void testUpdateUser() {
        User user = new User(null, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        userController.addUser(user);

        user.setName("Updated User");
        User updatedUser = userController.updateUser(user);

        assertEquals("Updated User", updatedUser.getName());
        assertEquals(user.getId(), updatedUser.getId());
    }

    @Test
    public void testUpdateNonExistingUser() {
        User user = new User(999L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        assertThrows(EntityNotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User(null, "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User(null, "user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        userController.addUser(user1);
        userController.addUser(user2);

        Collection<User> allUsers = userController.findAll();

        assertEquals(2, allUsers.size());
    }

    @Test
    public void testAutoSetNameIfBlank() {
        User user = new User(null, "test@example.com", "testlogin", "", LocalDate.of(1990, 1, 1));
        userController.addUser(user);
        assertEquals("testlogin", user.getName());
    }

}
