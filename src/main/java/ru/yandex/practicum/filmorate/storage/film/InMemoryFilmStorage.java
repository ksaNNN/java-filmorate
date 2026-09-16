package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * InMemoryFilmStorage.
 */
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validate(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Попытка обновить несуществующий фильм с id={}", film.getId());
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        validate(film);
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    public Film findById(Integer id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Фильм с id={} не найден", id);
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: описание длиннее 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка валидации: некорректная дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Ошибка валидации: продолжительность должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}