package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserController controller =
            new UserController(new UserService(new InMemoryUserStorage()));

    private User validUser() {
        User user = new User();
        user.setEmail("example@mail.ru");
        user.setLogin("xnx");
        user.setName("Cool Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        return user;
    }

    @Test
    void createValidUser_shouldSucceed() {
        assertDoesNotThrow(() -> controller.create(validUser()));
    }

    @Test
    void createUser_withoutAtInEmail_shouldThrow() {
        User user = validUser();
        user.setEmail("mail.ru");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void createUser_withSpaceInLogin_shouldThrow() {
        User user = validUser();
        user.setLogin("Bruh Brah");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void createUser_withBlankName_shouldUseLoginAsName() {
        User user = validUser();
        user.setName("");
        User created = controller.create(user);
        assertEquals(user.getLogin(), created.getName());
    }

    @Test
    void createUser_withFutureBirthday_shouldThrow() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(100));
        assertThrows(ValidationException.class, () -> controller.create(user));
    }
}