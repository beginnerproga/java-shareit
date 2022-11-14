package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDtoGateWay;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestDtoGateWay itemRequestDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestClient.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsPagination(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from, @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return itemRequestClient.getItemRequestsPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable long requestId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }


}

