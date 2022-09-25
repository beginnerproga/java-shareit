package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserNotHaveAccessException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
   private final ItemDao itemDao;
   private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("Received request to get all items from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        if (userDao.getUserById(userId) == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        return itemDao.getItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

    }


    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Received request to get a item by itemId={}", itemId);
        Item result = itemDao.getItemById(itemId);
        return ItemMapper.toItemDto(result);

    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        log.info("Received request to add item from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        if (userDao.getUserById(userId) == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        Item item = ItemMapper.toItem(itemDto, userId);
        itemDao.addItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, Long userId) {
        log.info("Received request to update item from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        if (userDao.getUserById(userId) == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        if (itemDao.getItemById(itemId).getOwner() != userId)
            throw new UserNotHaveAccessException("User with id=" + userId + " doesn't have access");

        Item item = ItemMapper.toItem(itemDto, userId);
        item = itemDao.updateItem(item, itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Received request to search all items with similar text");
        if (text.equals(""))
            return new ArrayList<>();
        return itemDao.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

    }
}
