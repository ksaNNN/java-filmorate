package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmController controller = new FilmController();

    private Film validFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фантастика про космос");
        film.setReleaseDate(LocalDate.of(2014, 11, 6));
        film.setDuration(169);
        return film;
    }

    @Test
    void createValidFilm_shouldSucceed() {
        Film film = validFilm();
        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void createFilm_withBlankName_shouldThrow() {
        Film film = validFilm();
        film.setName(" ");
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilm_withTooLongDescription_shouldThrow() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilm_withReleaseDateBeforeMin_shouldThrow() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilm_withReleaseDateExactlyMin_shouldSucceed() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void createFilm_withNonPositiveDuration_shouldThrow() {
        Film film = validFilm();
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.create(film));
    }
}