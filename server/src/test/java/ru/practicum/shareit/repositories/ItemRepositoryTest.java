package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "Nikita", "nikita@mail.ru"));
        user2 = userRepository.save(new User(2, "Qrew", "re@mail.ru"));
        item1 = itemRepository.save(new Item(1, "qerq", "qwrqeqw", true, user1));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1, "rer", user2, LocalDateTime.now()));
    }

    @Test
    public void searchGoodTest() {
        int size = 10;
        int page = 0;
        Page<Item> itemPage = itemRepository.search("qerq", PageRequest.of(page, size));
        assertNotNull(itemPage);
        assertEquals(1, itemPage.getContent().size());
        assertEquals(item1, itemPage.getContent().get(0));
    }

    @Test
    public void findByOwnerOrderByIdGoodTest() {
        int size = 10;
        int page = 0;
        Page<Item> itemPage = itemRepository.findByOwnerOrderById(user1, PageRequest.of(page, size));
        assertNotNull(itemPage);
        assertEquals(1, itemPage.getContent().size());
        assertEquals(item1, itemPage.getContent().get(0));

    }

    @Test
    public void findByItemRequestOrderByIdGoodTest() {
        item1.setItemRequest(itemRequest1);
        List<Item> items = itemRepository.findByItemRequestOrderById(itemRequest1);
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item1, items.get(0));
    }

}
