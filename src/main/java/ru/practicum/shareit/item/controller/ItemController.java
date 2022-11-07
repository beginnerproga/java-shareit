package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemInfoDto> getItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from, @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return itemService.getItems(userId,from,size);
    }


    @GetMapping("/{itemId}")
    public ItemInfoDto getItemById(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@Validated({Create.class}) @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfoDto addComment(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @RequestBody @Valid CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);


    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated({Update.class}) @RequestBody ItemDto itemDto, @PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from, @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return itemService.searchItems(text, from, size);
    }

}
