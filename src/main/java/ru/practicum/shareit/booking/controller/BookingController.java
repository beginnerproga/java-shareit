package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;
import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto acceptBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                        @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.acceptBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getBooking(@PathVariable Long bookingId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingInfoDto> getBookingsForBooker(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsForBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getBookingsForOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsForOwner(userId, state);
    }

}
