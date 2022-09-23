package ru.practicum.shareit.exceptions;

import java.net.BindException;

public class SameEmailException extends RuntimeException {
    public SameEmailException(String message) {
        super(message);
    }
}
