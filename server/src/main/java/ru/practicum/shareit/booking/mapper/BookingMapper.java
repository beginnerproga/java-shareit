package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User user, Status status) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), item, user, status);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getItem().getId(), booking.getStart(), booking.getEnd());
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return new BookingInfoDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }

}
