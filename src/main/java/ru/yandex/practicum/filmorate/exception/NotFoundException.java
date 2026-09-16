package ru.yandex.practicum.filmorate.exception;

/**
 * NotFoundException.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}