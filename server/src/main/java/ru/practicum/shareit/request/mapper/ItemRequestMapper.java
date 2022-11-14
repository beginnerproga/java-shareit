package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, LocalDateTime created) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), user, created);
    }

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestInfoDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.size() == 0 ? new ArrayList<>() : items.stream().map((item) -> new ItemRequestInfoDto.ItemForRequest(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getItemRequest().getId())).collect(Collectors.toList()));
    }

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest) {
        return new ItemRequestInfoDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }
}
