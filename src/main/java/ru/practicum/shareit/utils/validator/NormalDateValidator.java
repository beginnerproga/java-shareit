package ru.practicum.shareit.utils.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class NormalDateValidator implements ConstraintValidator<NormalDate, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart().isAfter(LocalDateTime.now()) && bookingDto.getEnd().getYear() < 2030;
    }
}
