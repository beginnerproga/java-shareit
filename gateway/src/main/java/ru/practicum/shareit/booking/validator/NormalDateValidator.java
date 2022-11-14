package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDtoGateWay;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class NormalDateValidator implements ConstraintValidator<NormalDate, BookItemRequestDtoGateWay> {
    @Override
    public boolean isValid(BookItemRequestDtoGateWay bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart().isAfter(LocalDateTime.now()) && bookingDto.getEnd().getYear() < 2030;
    }
}
