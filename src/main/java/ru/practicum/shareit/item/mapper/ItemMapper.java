package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item toItem(ItemDto itemDto, long userId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.isAvailable(), userId);
    }

}
