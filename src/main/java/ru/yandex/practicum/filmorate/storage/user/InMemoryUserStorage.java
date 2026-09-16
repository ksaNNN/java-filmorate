package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * InMemoryUserStorage.
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        validate(user);
        applyNameFallback(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя с id={}", user.getId());
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        validate(user);
        applyNameFallback(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public User findById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: некорректный логин {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void applyNameFallback(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}