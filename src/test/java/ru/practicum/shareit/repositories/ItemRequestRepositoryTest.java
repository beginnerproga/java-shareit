package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    private User user1;
    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "Nikita", "nikita@mail.ru"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1, "ewfe", user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

    }

    @Test
    public void findByUserOrderByCreatedDescGoodTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByUserOrderByCreatedDesc(user1);
        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest1, itemRequests.get(0));
    }

    @Test
    public void findOrderByCreatedDescGoodTest() {
        int page = 0;
        int size = 10;
        Page<ItemRequest> itemRequests = itemRequestRepository.findOrderByCreatedDesc(user1.getId(), PageRequest.of(page, size));
        assertNotNull(itemRequests);
        assertEquals(0, itemRequests.getContent().size());
    }
}
