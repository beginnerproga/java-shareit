package ru.practicum.shareit.utils.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateStartAndEndValidator implements ConstraintValidator<StartEarlyEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
       return bookingDto.getStart().isBefore(bookingDto.getEnd());

    }
}