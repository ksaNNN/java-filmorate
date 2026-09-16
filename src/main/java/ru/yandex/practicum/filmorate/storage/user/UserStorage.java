package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * UserStorage.
 */
public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User findById(Integer id);
}