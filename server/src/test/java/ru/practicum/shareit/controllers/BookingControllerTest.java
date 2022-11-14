package ru.practicum.shareit.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void addBookingGoodTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        long userId = 1;
        BookingInfoDto bookingInfoDto = new BookingInfoDto(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), null, null, Status.WAITING);

        when(bookingService.addBooking(bookingDto, userId)).thenReturn(bookingInfoDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingInfoDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item", is(bookingInfoDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingInfoDto.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString()), Status.class));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void addBookingWithNullUserIdBadTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        Long userId = null;

        when(bookingService.addBooking(bookingDto, userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("User's id is null")));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void addBookingWithNotFoundUserIdBadTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        long userId = 323232;

        when(bookingService.addBooking(bookingDto, userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void addBookingWithNotFoundItemIdBadTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        long userId = 2;

        when(bookingService.addBooking(bookingDto, userId)).thenThrow(new ItemNotFoundException("item with id=" + bookingDto.getItemId() + " not found"));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("item with id=" + bookingDto.getItemId() + " not found")));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void addBookingWithItemNotAvailableBadTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        long userId = 2;

        when(bookingService.addBooking(bookingDto, userId)).thenThrow(new ItemIsNotAvailableException("Item with id = " + bookingDto.getItemId() + " is not available for booking"));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Item with id = " + bookingDto.getItemId() + " is not available for booking")));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void addBookingWithOwnerBookingHisItemBadTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        long userId = 2;

        when(bookingService.addBooking(bookingDto, userId)).thenThrow(new OwnerCantBookingItemException("Owner can't book his item."));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Owner can't book his item.")));

        verify(bookingService, times(1)).addBooking(bookingDto, userId);

    }

    @Test
    public void acceptBookingGoodTest() throws Exception {
        long bookingId = 1;
        long userId = 1;
        BookingInfoDto bookingInfoDto = new BookingInfoDto(bookingId, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), null, null, Status.WAITING);

        when(bookingService.acceptBooking(bookingId, true, userId)).thenReturn(bookingInfoDto);
        bookingInfoDto.setStatus(Status.APPROVED);
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingInfoDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item", is(bookingInfoDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingInfoDto.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString()), Status.class));
        verify(bookingService, times(1)).acceptBooking(bookingId, true, userId);

    }

    @Test
    public void acceptBookingWithNullUserIdBadTest() throws Exception {
        long bookingId = 1;
        Long userId = null;
        when(bookingService.acceptBooking(bookingId, true, userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .queryParam("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("User's id is null")));
        verify(bookingService, times(1)).acceptBooking(bookingId, true, userId);

    }

    @Test
    public void acceptBookingWithNotFoundUserIdBadTest() throws Exception {
        long bookingId = 1;
        long userId = 3223;
        when(bookingService.acceptBooking(bookingId, true, userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));
        verify(bookingService, times(1)).acceptBooking(bookingId, true, userId);

    }

    @Test
    public void acceptBookingWithNotFoundBookingIdBadTest() throws Exception {
        long bookingId = 1323;
        long userId = 1;
        when(bookingService.acceptBooking(bookingId, true, userId)).thenThrow(new BookingNotFoundException("Booking with id=" + bookingId + " not found"));
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Booking with id=" + bookingId + " not found")));
        verify(bookingService, times(1)).acceptBooking(bookingId, true, userId);

    }


    @Test
    public void getBookingByIdGoodTest() throws Exception {
        long bookingId = 1;
        long userId = 1;
        BookingInfoDto bookingInfoDto = new BookingInfoDto(bookingId, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), null, null, Status.WAITING);
        when(bookingService.getBooking(bookingId, userId)).thenReturn(bookingInfoDto);
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingInfoDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item", is(bookingInfoDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingInfoDto.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString()), Status.class));
        verify(bookingService, times(1)).getBooking(bookingId, userId);

    }

    @Test
    public void getBookingByIdUserNotAccessBadTest() throws Exception {
        long bookingId = 1;
        long userId = 123;
        when(bookingService.getBooking(bookingId, userId)).thenThrow(new UserNotHaveAccessException("User with id = " + userId + " doesn't have access to change status of booking"));
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id = " + userId + " doesn't have access to change status of booking")));

        verify(bookingService, times(1)).getBooking(bookingId, userId);

    }

    @Test
    public void getBookingsForBookerGoodTest() throws Exception {
        long userId = 1;
        long bookingId1 = 1;
        long bookingId2 = 2;
        BookingInfoDto bookingInfoDto1 = new BookingInfoDto(bookingId1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), new Item(1, "efw", "ewqe", true, null), new User(1, "qwe", "wwq@mail.ru"), Status.WAITING);
        BookingInfoDto bookingInfoDto2 = new BookingInfoDto(bookingId2, LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(4).truncatedTo(ChronoUnit.SECONDS), new Item(2, "efwewf", "ewqe", true, null), new User(1, "qwe", "wwq@mail.ru"), Status.WAITING);
        List<BookingInfoDto> bookingInfoDtos = new ArrayList<>();
        bookingInfoDtos.add(bookingInfoDto1);
        bookingInfoDtos.add(bookingInfoDto2);
        when(bookingService.getBookingsForBooker(userId, "ALL", 0, 2)).thenReturn(bookingInfoDtos);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingInfoDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0]start", is(bookingInfoDtos.get(0).getStart().toString()), String.class))
                .andExpect(jsonPath("$.[0].end", is(bookingInfoDtos.get(0).getEnd().toString()), String.class))
                .andExpect(jsonPath("$.[0].item", is(bookingInfoDtos.get(0).getItem()), Item.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingInfoDtos.get(0).getBooker()), User.class))
                .andExpect(jsonPath("$.[0].status", is(bookingInfoDtos.get(0).getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.[1].id", is(bookingInfoDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1]start", is(bookingInfoDtos.get(1).getStart().toString()), String.class))
                .andExpect(jsonPath("$.[1].end", is(bookingInfoDtos.get(1).getEnd().toString()), String.class))
                .andExpect(jsonPath("$.[1].item", is(bookingInfoDtos.get(1).getItem()), Item.class))
                .andExpect(jsonPath("$.[1].booker", is(bookingInfoDtos.get(1).getBooker()), User.class))
                .andExpect(jsonPath("$.[1].status", is(bookingInfoDtos.get(1).getStatus().toString()), Status.class));

        verify(bookingService, times(1)).getBookingsForBooker(userId, "ALL", 0, 2);

    }

    @Test
    public void getBookingsForBookerIncorrectStateBadTest() throws Exception {
        long userId = 1;
        when(bookingService.getBookingsForBooker(userId, "AewfweL", 0, 2)).thenThrow(new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", "AewfweL")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));

        verify(bookingService, times(1)).getBookingsForBooker(userId, "AewfweL", 0, 2);

    }

    @Test
    public void getBookingsForOwnerGoodTest() throws Exception {
        long userId = 1;
        long bookingId1 = 1;
        long bookingId2 = 2;
        BookingInfoDto bookingInfoDto1 = new BookingInfoDto(bookingId1, LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), new Item(1, "efw", "ewqe", true, new User(2, "qgrewe", "wwqre@mail.ru")), new User(1, "qwe", "wwq@mail.ru"), Status.WAITING);
        BookingInfoDto bookingInfoDto2 = new BookingInfoDto(bookingId2, LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(4).truncatedTo(ChronoUnit.SECONDS), new Item(2, "efwewf", "ewqe", true, new User(2, "qgrewe", "wwqre@mail.ru")), new User(1, "qwe", "wwq@mail.ru"), Status.WAITING);
        List<BookingInfoDto> bookingInfoDtos = new ArrayList<>();
        bookingInfoDtos.add(bookingInfoDto1);
        bookingInfoDtos.add(bookingInfoDto2);
        when(bookingService.getBookingsForOwner(userId, "ALL", 0, 2)).thenReturn(bookingInfoDtos);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingInfoDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0]start", is(bookingInfoDtos.get(0).getStart().toString()), String.class))
                .andExpect(jsonPath("$.[0].end", is(bookingInfoDtos.get(0).getEnd().toString()), String.class))
                .andExpect(jsonPath("$.[0].item", is(bookingInfoDtos.get(0).getItem()), Item.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingInfoDtos.get(0).getBooker()), User.class))
                .andExpect(jsonPath("$.[0].status", is(bookingInfoDtos.get(0).getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.[1].id", is(bookingInfoDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1]start", is(bookingInfoDtos.get(1).getStart().toString()), String.class))
                .andExpect(jsonPath("$.[1].end", is(bookingInfoDtos.get(1).getEnd().toString()), String.class))
                .andExpect(jsonPath("$.[1].item", is(bookingInfoDtos.get(1).getItem()), Item.class))
                .andExpect(jsonPath("$.[1].booker", is(bookingInfoDtos.get(1).getBooker()), User.class))
                .andExpect(jsonPath("$.[1].status", is(bookingInfoDtos.get(1).getStatus().toString()), Status.class));

        verify(bookingService, times(1)).getBookingsForOwner(userId, "ALL", 0, 2);


    }

    @Test
    public void getBookingsForOwnerIncorrectStateBadTest() throws Exception {
        long userId = 1;
        when(bookingService.getBookingsForOwner(userId, "ewrew", 0, 2)).thenThrow(
                new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS"));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", "ewrew")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));

        verify(bookingService, times(1)).getBookingsForOwner(userId, "ewrew", 0, 2);


    }
}