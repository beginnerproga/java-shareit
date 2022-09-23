package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    List<Item> getItems(long userId);

    Item getItemById(long id);

    Item updateItem(Item item, long itemId);

    List<Item> searchItems(String text);

}
