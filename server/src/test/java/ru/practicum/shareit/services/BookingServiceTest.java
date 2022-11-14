package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    private BookingService bookingService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Booking booking1;
    private List<Booking> bookings;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "Qrew", "re@mail.ru");
        user3 = new User(3, "eqwewq", "kfk@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
        booking1 = new Booking(1, LocalDateTime.now().minusMinutes(12).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusMinutes(6).truncatedTo(ChronoUnit.SECONDS), item1, user2, Status.APPROVED);
        bookings = new ArrayList<>();
        bookings.add(booking1);
    }

    @Test
    public void addBookingGoodTest() {
        long userId = 2;
        long itemId = 1;
        booking1.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        BookingInfoDto result = bookingService.addBooking(BookingMapper.toBookingDto(booking1), userId);
        assertEquals(BookingMapper.toBookingInfoDto(booking1), result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(booking1);

    }

    @Test
    public void addBookingWithNotAvailableItemBadTest() {
        long userId = 2;
        long itemId = 1;
        item1.setAvailable(false);
        booking1.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        assertThrows(ItemIsNotAvailableException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking1), userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(0)).save(booking1);

    }

    @Test
    public void addBookingWithOwnerBookingItemBadTest() {
        long userId = 1;
        long itemId = 1;
        booking1.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        assertThrows(OwnerCantBookingItemException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking1), userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(0)).save(booking1);

    }

    @Test
    public void acceptBookingGoodTest() {
        long userId = 1;
        long bookingId = 1;
        booking1.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        BookingInfoDto result = bookingService.acceptBooking(bookingId, true, userId);
        assertEquals(BookingMapper.toBookingInfoDto(booking1), result);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(booking1);

    }

    @Test
    public void acceptBookingWithUserNotAccessBadTest() {
        long userId = 3;
        long bookingId = 1;
        booking1.setStatus(Status.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user3));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        assertThrows(UserNotHaveAccessException.class, () -> bookingService.acceptBooking(bookingId, true, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(0)).save(booking1);

    }

    @Test
    public void acceptBookingWithAlreadySetStatusBadTest() {
        long userId = 1;
        long bookingId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        assertThrows(StatusAlreadySetException.class, () -> bookingService.acceptBooking(bookingId, true, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(0)).save(booking1);

    }

    @Test
    public void getBookingGoodTest() {
        long userId = 1;
        long bookingId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking1));
        BookingInfoDto result = bookingService.getBooking(bookingId, userId);
        assertEquals(BookingMapper.toBookingInfoDto(booking1), result);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(bookingId);

    }

    @Test
    public void getBookingWithUserNotAccessBadTest() {
        long userId = 3;
        long bookingId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user3));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking1));
        assertThrows(UserNotHaveAccessException.class, () -> bookingService.getBooking(bookingId, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(bookingId);

    }

    @Test
    public void getBookingsForBookerGoodTest() {
        long userId = 2;
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;
        final PageImpl<Booking> pageBookings = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerOrderByStartDesc(user2, PageRequest.of(page, size))).thenReturn(pageBookings);
        List<BookingInfoDto> bookingInfoDtos = bookingService.getBookingsForBooker(userId, state, from, size);
        assertEquals(1, bookingInfoDtos.size());
        assertEquals(BookingMapper.toBookingInfoDto(booking1), bookingInfoDtos.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findByBookerOrderByStartDesc(user2, PageRequest.of(page, size));
    }

    @Test
    public void getBookingsForOwnerGoodTest() {
        long userId = 1;
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;
        final PageImpl<Booking> pageBookings = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size))).thenReturn(pageBookings);
        List<BookingInfoDto> bookingInfoDtos = bookingService.getBookingsForOwner(userId, state, from, size);
        assertEquals(1, bookingInfoDtos.size());
        assertEquals(BookingMapper.toBookingInfoDto(booking1), bookingInfoDtos.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size));

    }

    @Test
    public void getBookingsForBookerWithUnknownStateBadTest() {
        long userId = 1;
        String state = "SHEEESH";
        int from = 0;
        int size = 10;
        int page = 0;
        final PageImpl<Booking> pageBookings = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size))).thenReturn(pageBookings);
        assertThrows(IncorrectStateException.class, () -> bookingService.getBookingsForOwner(userId, state, from, size));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(0)).findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size));

    }

    @Test
    public void getBookingsForOwnerWithUnknownState() {
        long userId = 1;
        String state = "SHEESH";
        int from = 0;
        int size = 10;
        int page = 0;
        final PageImpl<Booking> pageBookings = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size))).thenReturn(pageBookings);
        assertThrows(IncorrectStateException.class, () -> bookingService.getBookingsForOwner(userId, state, from, size));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(0)).findByItem_OwnerOrderByStartDesc(user1, PageRequest.of(page, size));
    }
}