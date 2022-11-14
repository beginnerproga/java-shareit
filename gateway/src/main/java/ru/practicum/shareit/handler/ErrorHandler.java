package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exception.IncorrectStateException;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {BookingController.class})
public class ErrorHandler {

    @ExceptionHandler(value = {IncorrectStateException.class})
    public ResponseEntity<Map<String, String>> handleInternalServerErrorException(final IncorrectStateException e) {
        log.error("Server returned HttpCode 500. {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}