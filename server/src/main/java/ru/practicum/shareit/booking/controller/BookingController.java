package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingInfoDto addBooking(@RequestBody BookingDto bookingDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto acceptBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                        @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.acceptBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getBookingById(@PathVariable Long bookingId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingInfoDto> getBookingsForBooker(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                     @RequestParam String state,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
        return bookingService.getBookingsForBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getBookingsForOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                    @RequestParam String state,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        return bookingService.getBookingsForOwner(userId, state, from, size);
    }

}
