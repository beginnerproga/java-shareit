package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.validator.NormalDate;
import ru.practicum.shareit.utils.validator.StartEarlyEnd;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@StartEarlyEnd
@NormalDate
public class BookingDto {

    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}
