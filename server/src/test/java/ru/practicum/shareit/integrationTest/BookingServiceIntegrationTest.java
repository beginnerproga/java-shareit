package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "jnjn", "re@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
        booking1 = new Booking(1, LocalDateTime.now().minusMinutes(12).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusMinutes(6).truncatedTo(ChronoUnit.SECONDS), item1, user2, Status.APPROVED);
        userService.addUser(UserMapper.toUserDto(user1));
        itemService.addItem(ItemMapper.toItemDto(item1), user1.getId());
        userService.addUser(UserMapper.toUserDto(user2));
        bookingService.addBooking(BookingMapper.toBookingDto(booking1), user2.getId());

    }

    @Test
    public void acceptBookingGoodTest() {
        BookingInfoDto result = bookingService.acceptBooking(booking1.getId(), true, user1.getId());
        booking1.setStatus(Status.APPROVED);
        result.setStart(result.getStart().truncatedTo(ChronoUnit.SECONDS));
        result.setEnd(result.getEnd().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(BookingMapper.toBookingInfoDto(booking1), result);
    }

    @Test
    public void getBookingsForOwnerGoodTest() {
        bookingService.acceptBooking(booking1.getId(), true, user1.getId());
        List<BookingInfoDto> bookingInfoDtos = bookingService.getBookingsForOwner(1L, "ALL", 0, 10);
        assertEquals(1, bookingInfoDtos.size());
        bookingInfoDtos.get(0).setStart(bookingInfoDtos.get(0).getStart().truncatedTo(ChronoUnit.SECONDS));
        bookingInfoDtos.get(0).setEnd(bookingInfoDtos.get(0).getEnd().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(BookingMapper.toBookingInfoDto(booking1), bookingInfoDtos.get(0));
        List<BookingInfoDto> bookingInfoDtos1 = bookingService.getBookingsForOwner(1L, "PAST", 0, 10);
        assertEquals(1, bookingInfoDtos1.size());
        List<BookingInfoDto> bookingInfoDtos2 = bookingService.getBookingsForOwner(1L, "FUTURE", 0, 10);
        assertEquals(0, bookingInfoDtos2.size());
        List<BookingInfoDto> bookingInfoDtos3 = bookingService.getBookingsForOwner(1L, "REJECTED", 0, 10);
        assertEquals(0, bookingInfoDtos3.size());

    }

    @Test
    public void getBookingsForBookerGoodTest() {
        bookingService.acceptBooking(booking1.getId(), true, user1.getId());
        List<BookingInfoDto> bookingInfoDtos = bookingService.getBookingsForBooker(2L, "ALL", 0, 10);
        assertEquals(1, bookingInfoDtos.size());
        bookingInfoDtos.get(0).setStart(bookingInfoDtos.get(0).getStart().truncatedTo(ChronoUnit.SECONDS));
        bookingInfoDtos.get(0).setEnd(bookingInfoDtos.get(0).getEnd().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(BookingMapper.toBookingInfoDto(booking1), bookingInfoDtos.get(0));
        List<BookingInfoDto> bookingInfoDtos1 = bookingService.getBookingsForBooker(2L, "PAST", 0, 10);
        assertEquals(1, bookingInfoDtos1.size());
        List<BookingInfoDto> bookingInfoDtos2 = bookingService.getBookingsForBooker(2L, "FUTURE", 0, 10);
        assertEquals(0, bookingInfoDtos2.size());
        List<BookingInfoDto> bookingInfoDtos3 = bookingService.getBookingsForBooker(2L, "REJECTED", 0, 10);
        assertEquals(0, bookingInfoDtos3.size());
        assertThrows(UserIdWasNotTransferredException.class, () -> bookingService.getBookingsForBooker(null, "ALL", 0, 10));
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingsForBooker(55L, "ALL", 0, 10));


    }

    @Test
    public void addBookingBadTest() {
        assertThrows(UserIdWasNotTransferredException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking1), null));
        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking1), 55L));

    }

}
