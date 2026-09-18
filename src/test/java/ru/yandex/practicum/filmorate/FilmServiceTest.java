package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(new InMemoryFilmStorage(), userStorage);
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(120);
        return filmService.create(film);
    }

    private User createUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.ru");
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userService.create(user);
    }

    @Test
    void addLike_shouldIncreaseLikesCount() {
        Film film = createFilm("Фильм 1");
        User user = createUser("user1");

        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, film.getLikes().size());
        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    void addLike_toNonExistentFilm_shouldThrowNotFound() {
        User user = createUser("user1");
        assertThrows(NotFoundException.class, () -> filmService.addLike(999, user.getId()));
    }

    @Test
    void addLike_byNonExistentUser_shouldThrowNotFound() {
        Film film = createFilm("Фильм 1");
        assertThrows(NotFoundException.class, () -> filmService.addLike(film.getId(), 999));
    }

    @Test
    void removeLike_shouldDecreaseLikesCount() {
        Film film = createFilm("Фильм 1");
        User user = createUser("user1");
        filmService.addLike(film.getId(), user.getId());

        filmService.removeLike(film.getId(), user.getId());

        assertEquals(0, film.getLikes().size());
    }

    @Test
    void getPopular_shouldReturnFilmsSortedByLikesDescending() {
        Film film1 = createFilm("Фильм 1");
        Film film2 = createFilm("Фильм 2");
        User user1 = createUser("user1");
        User user2 = createUser("user2");

        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film1.getId(), user1.getId());

        List<Film> popular = filmService.getPopular(10);

        assertEquals(film2.getId(), popular.get(0).getId());
        assertEquals(film1.getId(), popular.get(1).getId());
    }

    @Test
    void getPopular_withZeroCount_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> filmService.getPopular(0));
    }

    @Test
    void getPopular_withNegativeCount_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> filmService.getPopular(-5));
    }

    @Test
    void getPopular_withNullCount_shouldReturnDefaultLimit() {
        createFilm("Фильм 1");
        List<Film> popular = filmService.getPopular(null);
        assertEquals(1, popular.size());
    }
}