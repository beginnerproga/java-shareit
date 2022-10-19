package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.isAvailable(), user);
    }

    public static ItemInfoDto toItemInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return new ItemInfoDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(),
                lastBooking != null ? new ItemInfoDto.ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new ItemInfoDto.ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null,
                comments != null ? comments.stream().map(CommentMapper::toCommentInfoDto).collect(Collectors.toList()) : new ArrayList<>());
    }

}
