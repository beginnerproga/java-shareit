package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentException;
import ru.practicum.shareit.exceptions.UserNotHaveAccessException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;
    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private List<Item> items;
    private Comment comment1;
    private List<Comment> comments;
    private List<Booking> bookings;
    private Item item3;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "Qrew", "re@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
        item2 = new Item(2, "qwewqeqw", "fwfw", true, user1);
        item3 = new Item(3, "fwe", "fefe", true, user2);
        items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        comment1 = new Comment(1, "rewrer", item1, user2, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Comment comment2 = new Comment(2, "wefwfew", item1, user2, LocalDateTime.now().minusMinutes(23).truncatedTo(ChronoUnit.SECONDS));
        comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        booking5 = new Booking(1, LocalDateTime.now().minusMinutes(12), LocalDateTime.now().minusMinutes(6), item1, user2, Status.APPROVED);
        booking2 = null;
        booking3 = null;
        booking4 = null;
        booking1 = null;
        bookings = new ArrayList<>();
        bookings.add(booking5);
    }

    @Test
    public void getItemsGoodTest() {
        int size = 10;
        int page = 0;
        long userId = 1;
        final PageImpl<Item> pageItems = new PageImpl<>(items);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findByOwnerOrderById(user1, PageRequest.of(page, size))).thenReturn(pageItems);
        when(commentRepository.findByItemOrderById(item1)).thenReturn(comments);
        when(commentRepository.findByItemOrderById(item2)).thenReturn(new ArrayList<>());
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(item1, user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Status.APPROVED)).thenReturn(booking1);
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(item1, user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Status.APPROVED)).thenReturn(booking2);
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(item2, user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Status.APPROVED)).thenReturn(booking3);
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(item2, user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Status.APPROVED)).thenReturn(booking4);
        List<ItemInfoDto> itemInfoDto = itemService.getItems(userId, 0, 10);
        assertEquals(2, itemInfoDto.size());
        assertEquals(ItemMapper.toItemInfoDto(item1, booking1, booking2, comments), itemInfoDto.get(0));
        assertEquals(ItemMapper.toItemInfoDto(item2, booking3, booking4, new ArrayList<>()), itemInfoDto.get(1));
        verify(itemRepository, times(1)).findByOwnerOrderById(user1, PageRequest.of(page, size));
        verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, times(1)).findByItemOrderById(item1);
        verify(commentRepository, times(1)).findByItemOrderById(item2);
        verify(bookingRepository, times(2)).findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(any(Item.class), any(User.class), any(LocalDateTime.class), any(Status.class));
        verify(bookingRepository, times(2)).findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(any(Item.class), any(User.class), any(LocalDateTime.class), any(Status.class));


    }

    @Test
    public void getItemByIdGoodTest() {
        long userId = 1;
        long itemId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(item1, user1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Status.APPROVED)).thenReturn(booking1);
        when(bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(item1, user1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Status.APPROVED)).thenReturn(booking2);
        when(commentRepository.findByItemOrderById(item1)).thenReturn(comments);
        ItemInfoDto result = itemService.getItemById(itemId, userId);
        assertEquals(ItemMapper.toItemInfoDto(item1, booking1, booking2, comments), result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(any(Item.class), any(User.class), any(LocalDateTime.class), any(Status.class));
        verify(bookingRepository, times(1)).findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(any(Item.class), any(User.class), any(LocalDateTime.class), any(Status.class));

    }

    @Test
    public void addItemGoodTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        ItemDto result = itemService.addItem(itemDto, userId);
        assertEquals(ItemMapper.toItemDto(item1), result);
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    public void updateItemGoodTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item2);
        long itemId = 1;
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.getReferenceById(itemId)).thenReturn(item1);
        when(itemRepository.save(item1)).thenReturn(item1);
        ItemDto result = itemService.updateItem(itemDto, itemId, userId);
        item2.setId(1);
        assertEquals(ItemMapper.toItemDto(item2), result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(2)).getReferenceById(itemId);

    }

    @Test
    public void updateItemWithUserNotHaveAccessBadTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item2);
        long itemId = 3;
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.getReferenceById(itemId)).thenReturn(item3);
        when(itemRepository.save(item1)).thenReturn(item1);
        assertThrows(UserNotHaveAccessException.class, () -> itemService.updateItem(itemDto, itemId, userId));
    }

    @Test
    public void searchItemsGoodTest() {
        String text = "123";
        int from = 0;
        int page = 0;
        int size = 10;
        final PageImpl<Item> pageItems = new PageImpl<>(items);

        when(itemRepository.search(text, PageRequest.of(page, size))).thenReturn(pageItems);
        List<ItemDto> result = itemService.searchItems(text, from, size);
        assertArrayEquals(result.toArray(), items.stream().map(ItemMapper::toItemDto).toArray());
        verify(itemRepository, times(1)).search(text, PageRequest.of(page, size));

    }

    @Test
    public void addCommentGoodTest() {
        long userId = 2;
        long itemId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.findByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class), any(Item.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.save(comment1)).thenReturn(comment1);
        CommentInfoDto result = itemService.addComment(itemId, userId, new CommentDto(comment1.getId(), comment1.getText()));
        result.setCreated(result.getCreated().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(new CommentInfoDto(comment1.getId(), comment1.getText(), comment1.getAuthor().getName(), comment1.getCreated()), result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class), any(Item.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));

    }

    @Test
    public void addCommentWithNotBookingBadTest() {
        long userId = 2;
        long itemId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingRepository.findByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class), any(Item.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(commentRepository.save(comment1)).thenReturn(comment1);
        assertThrows(CommentException.class, () -> itemService.addComment(itemId, userId, new CommentDto(comment1.getId(), comment1.getText())));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class), any(Item.class), any(LocalDateTime.class));
        verify(commentRepository, times(0)).save(any(Comment.class));

    }

}
