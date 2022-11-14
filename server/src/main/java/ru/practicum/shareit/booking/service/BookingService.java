package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import java.util.List;

public interface BookingService {
    BookingInfoDto addBooking(BookingDto bookingDto, Long userId);

    BookingInfoDto acceptBooking(Long bookingId, boolean approved, Long userId);

    BookingInfoDto getBooking(Long bookingId, Long userId);

    List<BookingInfoDto> getBookingsForBooker(Long userId, String state, int from, int size);

    List<BookingInfoDto> getBookingsForOwner(Long userId, String state, int from, int size);
}
