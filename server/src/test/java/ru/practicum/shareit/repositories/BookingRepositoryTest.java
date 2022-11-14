package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "Nikita", "nikita@mail.ru"));
        user2 = userRepository.save(new User(2, "Qrew", "re@mail.ru"));
        user3 = userRepository.save(new User(3, "Qrew", "revdsv@mail.ru"));
        item1 = itemRepository.save(new Item(1, "qerq", "qwrqeqw", true, user1));
        item2 = itemRepository.save(new Item(2, "fd", "fdsfds", true, user2));
        booking1 = bookingRepository.save(new Booking(1, LocalDateTime.now().minusMinutes(12).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().minusMinutes(5).truncatedTo(ChronoUnit.SECONDS), item1, user2, Status.APPROVED));
        booking2 = bookingRepository.save(new Booking(2, LocalDateTime.now().minusMinutes(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusMinutes(5).truncatedTo(ChronoUnit.SECONDS), item2, user3, Status.REJECTED));

    }

    @Test
    public void findByBookerOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookingPage = bookingRepository.findByBookerOrderByStartDesc(user2, PageRequest.of(page, size));
        assertNotNull(bookingPage);
        assertEquals(1, bookingPage.getContent().size());
        assertEquals(booking1, bookingPage.getContent().get(0));

    }

    @Test
    public void findByBookerAndStatusOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookingPage = bookingRepository.findByBookerAndStatusOrderByStartDesc(user2, Status.APPROVED, PageRequest.of(page, size));
        assertNotNull(bookingPage);
        assertEquals(1, bookingPage.getContent().size());
        assertEquals(booking1, bookingPage.getContent().get(0));
        Page<Booking> bookingPage1 = bookingRepository.findByBookerAndStatusOrderByStartDesc(user2, Status.REJECTED, PageRequest.of(page, size));
        assertNotNull(bookingPage1);
        assertEquals(0, bookingPage1.getContent().size());
        Page<Booking> bookingPage2 = bookingRepository.findByBookerAndStatusOrderByStartDesc(user3, Status.REJECTED, PageRequest.of(page, size));
        assertNotNull(bookingPage2);
        assertEquals(1, bookingPage2.getContent().size());
        assertEquals(booking2, bookingPage2.getContent().get(0));

    }

    @Test
    public void findByBookerAndStartBeforeAndEndAfterOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookingPage = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user2, LocalDateTime.now(), LocalDateTime.now().minusMinutes(6), PageRequest.of(page, size));
        assertNotNull(bookingPage);
        assertEquals(1, bookingPage.getContent().size());
        assertEquals(booking1, bookingPage.getContent().get(0));
        Page<Booking> bookingPage1 = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user2, LocalDateTime.now(), LocalDateTime.now().minusMinutes(1), PageRequest.of(page, size));
        assertNotNull(bookingPage1);
        assertEquals(0, bookingPage1.getContent().size());

    }

    @Test
    public void findByBookerAndStartAfterOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookingPage = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user2, LocalDateTime.now().minusDays(1), PageRequest.of(page, size));
        assertNotNull(bookingPage);
        assertEquals(1, bookingPage.getContent().size());
        assertEquals(booking1, bookingPage.getContent().get(0));
        Page<Booking> bookingPage1 = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user2, LocalDateTime.now(), PageRequest.of(page, size));
        assertNotNull(bookingPage1);
        assertEquals(0, bookingPage1.getContent().size());

    }

    @Test
    public void findByBookerAndEndBeforeOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookingPage = bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user2, LocalDateTime.now().plusHours(3), PageRequest.of(page, size));
        assertNotNull(bookingPage);
        assertEquals(1, bookingPage.getContent().size());
        assertEquals(booking1, bookingPage.getContent().get(0));
        Page<Booking> bookingPage1 = bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user2, LocalDateTime.now().minusMinutes(100), PageRequest.of(page, size));
        assertNotNull(bookingPage1);
        assertEquals(0, bookingPage1.getContent().size());

    }

    @Test
    public void findByBookerAndItemAndEndBeforeOrderByStartDescGoodTest() {
        List<Booking> bookings = bookingRepository.findByBookerAndItemAndEndBeforeOrderByStartDesc(user2, item1, LocalDateTime.now().plusHours(3));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
        List<Booking> bookings1 = bookingRepository.findByBookerAndItemAndEndBeforeOrderByStartDesc(user3, item2, LocalDateTime.now().plusHours(3));
        assertNotNull(bookings1);
        assertEquals(1, bookings1.size());
        assertEquals(booking2, bookings1.get(0));

    }

    @Test
    public void findByItem_OwnerOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookings = bookingRepository.findByItem_OwnerOrderByStartDesc(user2, PageRequest.of(page, size));
        assertNotNull(bookings);
        assertEquals(1, bookings.getContent().size());
        assertEquals(booking2, bookings.getContent().get(0));
    }

    @Test
    public void findByItem_OwnerAndStatusOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookings = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(user2, Status.REJECTED, PageRequest.of(page, size));
        assertNotNull(bookings);
        assertEquals(1, bookings.getContent().size());
        assertEquals(booking2, bookings.getContent().get(0));
        Page<Booking> bookings1 = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(user1, Status.WAITING, PageRequest.of(page, size));
        assertNotNull(bookings1);
        assertEquals(0, bookings1.getContent().size());

    }

    @Test
    public void findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookings = bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(user2, LocalDateTime.now(), LocalDateTime.now().minusDays(2), PageRequest.of(page, size));
        assertNotNull(bookings);
        assertEquals(1, bookings.getContent().size());
        assertEquals(booking2, bookings.getContent().get(0));
        Page<Booking> bookings1 = bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(user2, LocalDateTime.now(), LocalDateTime.now().plusHours(5), PageRequest.of(page, size));
        assertNotNull(bookings1);
        assertEquals(0, bookings1.getContent().size());

    }

    @Test
    public void findByItem_OwnerAndStartAfterOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookings = bookingRepository.findByItem_OwnerAndStartAfterOrderByStartDesc(user2, LocalDateTime.now().minusDays(2), PageRequest.of(page, size));
        assertNotNull(bookings);
        assertEquals(1, bookings.getContent().size());
        assertEquals(booking2, bookings.getContent().get(0));
    }

    @Test
    public void findByItem_OwnerAndEndBeforeOrderByStartDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<Booking> bookings = bookingRepository.findByItem_OwnerAndEndBeforeOrderByStartDesc(user2, LocalDateTime.now().plusDays(2), PageRequest.of(page, size));
        assertNotNull(bookings);
        assertEquals(1, bookings.getContent().size());
        assertEquals(booking2, bookings.getContent().get(0));

    }

    @Test
    public void findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDescGoodTest() {
        Booking booking = bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(item1, user1, LocalDateTime.now().plusDays(3), Status.APPROVED);
        assertNotNull(booking);
        assertEquals(booking1, booking);
    }

    @Test
    public void findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAscGoodTest() {
        Booking booking = bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(item1, user1, LocalDateTime.now().minusDays(3), Status.APPROVED);
        assertNotNull(booking);
        assertEquals(booking1, booking);

    }
}
