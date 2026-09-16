package ru.yandex.practicum.filmorate.exception;

/**
 * ValidationException.
 */

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}