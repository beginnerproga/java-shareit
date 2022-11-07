package ru.practicum.shareit.repositories;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    private Comment comment1;
    private Comment comment2;
    private Item item1;
    private User user1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "Nikita", "nikita@mail.ru"));
        item1 = itemRepository.save(new Item(1, "qerq", "qwrqeqw", true, user1));
        comment1 = commentRepository.save(new Comment(1, "3r3", item1, user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        comment2 = commentRepository.save(new Comment(2, "reve", item1, user1, LocalDateTime.now().minusHours(3).truncatedTo(ChronoUnit.SECONDS)));

    }

    @Test
    public void findByItemOrderByIdGoodTest() {
        List<Comment> commentList = commentRepository.findByItemOrderById(item1);
        assertNotNull(commentList);
        assertEquals(2, commentList.size());
        assertEquals(comment1, commentList.get(0));
        assertEquals(comment2, commentList.get(1));
    }
}
