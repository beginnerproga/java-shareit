package ru.practicum.shareit.exceptions;

public class UserNotHaveAccessException extends RuntimeException {
    public UserNotHaveAccessException(String message) {
        super(message);
    }
}
