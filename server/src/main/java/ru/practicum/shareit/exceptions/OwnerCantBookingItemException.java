package ru.practicum.shareit.exceptions;

public class OwnerCantBookingItemException extends RuntimeException {
    public OwnerCantBookingItemException(String message) {
        super(message);
    }
}
