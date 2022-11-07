package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ItemRequestInfoDto addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getItemRequestsPagination(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from, @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return itemRequestService.getItemRequestsPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getItemRequestById(@PathVariable long requestId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }


}
