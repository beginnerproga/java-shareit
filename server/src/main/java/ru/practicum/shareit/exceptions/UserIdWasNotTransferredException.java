package ru.practicum.shareit.exceptions;

public class UserIdWasNotTransferredException extends RuntimeException {
    public UserIdWasNotTransferredException(String message) {
        super(message);
    }
}
