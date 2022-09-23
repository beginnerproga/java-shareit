package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest itemRequest;

    public Item(long id, String name, String description, Boolean available, long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    public Boolean isAvailable() {
        return available;
    }
}
