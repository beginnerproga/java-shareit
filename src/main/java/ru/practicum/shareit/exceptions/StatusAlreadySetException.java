package ru.practicum.shareit.exceptions;

public class StatusAlreadySetException extends RuntimeException {
    public StatusAlreadySetException(String message) {
        super(message);
    }
}
