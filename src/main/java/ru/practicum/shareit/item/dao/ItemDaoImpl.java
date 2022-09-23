package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    HashMap<Long, Item> items = new HashMap<>();
    long counts = 1;

    @Override
    public Item addItem(Item item) {
        item.setId(counts);
        items.put(counts, item);
        counts++;
        return item;
    }

    @Override
    public List<Item> getItems(long userId) {
        return items.values().stream().filter(item -> item.getOwner() == userId).collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        if (item.getName() != null)
            items.get(itemId).setName(item.getName());
        if (item.getDescription() != null)
            items.get(itemId).setDescription(item.getDescription());
        if (item.isAvailable() != null)
            items.get(itemId).setAvailable(item.isAvailable());
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream().filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.isAvailable()).collect(Collectors.toList());
    }
}
