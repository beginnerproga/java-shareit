package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.SameEmailException;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserNotHaveAccessException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class})
public class ErrorHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final MethodArgumentNotValidException e) {
        log.error("Server returned HttpCode 400. {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = {SameEmailException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final SameEmailException e) {
        log.error("Server returned HttpCode 500. {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(value = {UserNotFoundException.class, UserNotHaveAccessException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final RuntimeException e) {
        log.error("Server returned HttpCode 404. {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = {UserIdWasNotTransferredException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final UserIdWasNotTransferredException e) {
        log.error("Server returned HttpCode 400. {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
