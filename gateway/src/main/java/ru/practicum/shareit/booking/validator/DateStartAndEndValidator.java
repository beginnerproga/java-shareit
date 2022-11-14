package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDtoGateWay;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateStartAndEndValidator implements ConstraintValidator<StartEarlyEnd, BookItemRequestDtoGateWay> {

    @Override
    public boolean isValid(BookItemRequestDtoGateWay bookingDto, ConstraintValidatorContext constraintValidatorContext) {
       return bookingDto.getStart().isBefore(bookingDto.getEnd());

    }
}