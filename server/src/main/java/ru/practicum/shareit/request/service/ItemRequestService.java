package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestInfoDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestInfoDto> getItemRequests(Long userId);

    List<ItemRequestInfoDto> getItemRequestsPagination(Long userId, Integer from, Integer size);

    ItemRequestInfoDto getItemRequestById(long requestId, Long userId);
}
