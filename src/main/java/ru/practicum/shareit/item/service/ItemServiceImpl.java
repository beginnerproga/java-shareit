package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<ItemInfoDto> getItems(Long userId) {
        log.info("Received request to get all items from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        List<Item> items = itemRepository.findByOwnerOrderById(user);
        List<ItemInfoDto> itemInfoDto = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findByItemOrderById(item);
            Booking last = bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(item, user, LocalDateTime.now(), Status.APPROVED);
            Booking next = bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(item, user, LocalDateTime.now(), Status.APPROVED);
            itemInfoDto.add(ItemMapper.toItemInfoDto(item, last, next, comments));
        }
        return itemInfoDto;
    }


    @Override
    public ItemInfoDto getItemById(Long itemId, Long userId) {
        log.info("Received request to get a item by itemId={}", itemId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Item result = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Item with id=" + itemId + " not found");
        });
        List<Comment> comments = commentRepository.findByItemOrderById(result);
        Booking last = bookingRepository.findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(result, user, LocalDateTime.now(), Status.APPROVED);
        Booking next = bookingRepository.findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(result, user, LocalDateTime.now(), Status.APPROVED);
        return ItemMapper.toItemInfoDto(result, last, next, comments);

    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        log.info("Received request to add item from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Item item = ItemMapper.toItem(itemDto, user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Received request to update item from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        if (itemRepository.getReferenceById(itemId).getOwner().getId() != userId)
            throw new UserNotHaveAccessException("User with id=" + userId + " doesn't have access");

        Item item = ItemMapper.toItem(itemDto, userRepository.getReferenceById(userId));
        Item result = itemRepository.getReferenceById(itemId);
        if (item.getName() != null && !item.getName().isBlank())
            result.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            result.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            result.setAvailable(item.getAvailable());
        item = itemRepository.save(result);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Received request to search all items with similar text");
        if (text.equals(""))
            return new ArrayList<>();
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

    }

    @Override
    public CommentInfoDto addComment(long itemId, Long userId, CommentDto commentDto) {
        log.info("Received request to add comment for user with user's id = " + userId + " for item's id = " + itemId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Item with id=" + itemId + " not found");
        });
        if (bookingRepository.findByBookerAndItemAndEndBeforeOrderByStartDesc(user, item, LocalDateTime.now()).isEmpty())
            throw new CommentException("User cannot to add comment, because he doesn't book");
        Comment comment = CommentMapper.toComment(commentDto, user, item, LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentInfoDto(comment);

    }
}
