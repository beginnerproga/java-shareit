package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDtoGateWay;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.IncorrectStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsForBooker(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={} - gateway request", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}  - gateway request", stateParam, userId, from, size);
        return bookingClient.getBookingsForOwner(userId, state, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                             @RequestBody @Valid BookItemRequestDtoGateWay requestDto) {
        log.info("Creating booking {}, userId={} - gateway request", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={} - gateway request", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> acceptBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("accept booking {}, userId={}, status = {} - gateway request", bookingId, userId, approved);
        return bookingClient.acceptBooking(bookingId, approved, userId);
    }

}
