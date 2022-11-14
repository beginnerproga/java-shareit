package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestInfoDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getItemRequestsPagination(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestService.getItemRequestsPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getItemRequestById(@PathVariable long requestId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

}
