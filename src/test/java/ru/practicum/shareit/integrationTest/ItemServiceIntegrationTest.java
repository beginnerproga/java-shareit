package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private User user1;
    private User user2;
    private Item item1;
    private Item item3;

    private Booking booking5;


    @BeforeEach
    public void beforeEach() {
        user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "jnjn", "re@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
        item3 = new Item(3, "qerqq", "qwrqeqw", true, user2);
        booking5 = new Booking(1, LocalDateTime.now().minusMinutes(12), LocalDateTime.now().minusMinutes(6), item1, user2, Status.APPROVED);
        userService.addUser(UserMapper.toUserDto(user1));
        itemService.addItem(ItemMapper.toItemDto(item1), user1.getId());
        userService.addUser(UserMapper.toUserDto(user2));
        itemService.addItem(ItemMapper.toItemDto(item3), user2.getId());
        bookingService.addBooking(BookingMapper.toBookingDto(booking5), 2L);

    }

    @Test
    public void getItemsGoodTest() {
        List<ItemInfoDto> itemInfoDtos = itemService.getItems(user1.getId(), 0, 10);
        assertNotNull(itemInfoDtos);
        assertEquals(1, itemInfoDtos.size());
        assertEquals(ItemMapper.toItemInfoDto(item1, null, null, new ArrayList<>()), itemInfoDtos.get(0));

    }

    @Test
    public void addItemBadTest() {
        assertThrows(UserNotFoundException.class, () -> itemService.addItem(new ItemDto("dew", "we", true), 5L));
        assertThrows(UserIdWasNotTransferredException.class, () -> itemService.addItem(new ItemDto("dew", "we", true), null));
    }

    @Test
    public void updateItemBadTest() {
        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(new ItemDto("dew", "we", true), 1L, 5L));
        assertThrows(UserIdWasNotTransferredException.class, () -> itemService.updateItem(new ItemDto("dew", "we", true), 1L, null));
    }

    @Test
    public void getItemByIdGoodTest() {
        long userId = 1;
        long itemId = 1;
        ItemInfoDto result = itemService.getItemById(itemId, userId);
        assertEquals(ItemMapper.toItemInfoDto(item1, null, null, null), result);

    }

    @Test
    public void searchItemsGoodTest() {
        List<ItemDto> itemDtos = itemService.searchItems("qwrqeqw", 0, 10);
        assertEquals(2, itemDtos.size());
        assertEquals(ItemMapper.toItemDto(item1), itemDtos.get(0));
        item3.setId(2);
        assertEquals(ItemMapper.toItemDto(item3), itemDtos.get(1));

    }

    @Test
    public void addCommentGoodTest() {
        CommentInfoDto commentInfoDto = itemService.addComment(1, 2L, new CommentDto(1, "fe"));
        commentInfoDto.setCreated(commentInfoDto.getCreated().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(new CommentInfoDto(1L, "fe", "jnjn", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)), commentInfoDto);
    }

}
