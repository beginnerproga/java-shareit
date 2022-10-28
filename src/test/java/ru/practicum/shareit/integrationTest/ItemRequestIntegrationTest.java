package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private User user1;
    private User user2;
    private Item item1;
    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "jnjn", "re@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
    }

    @Test
    public void getItemRequestsPaginationGoodTest() {
        itemRequest1 = new ItemRequest(1, "qwe", user2, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        item1.setItemRequest(itemRequest1);
        userService.addUser(UserMapper.toUserDto(user1));
        userService.addUser(UserMapper.toUserDto(user2));
        itemRequestService.addItemRequest(new ItemRequestDto(1, "qwe"), 2L);
        itemService.addItem(ItemMapper.toItemDto(item1), user1.getId());
        List<ItemRequestInfoDto> itemRequestInfoDto = itemRequestService.getItemRequestsPagination(1L, 0, 10);
        assertEquals(1, itemRequestInfoDto.size());
        itemRequestInfoDto.get(0).setCreated(itemRequestInfoDto.get(0).getCreated().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(ItemRequestMapper.toItemRequestInfoDto(itemRequest1, Collections.singletonList(item1)), itemRequestInfoDto.get(0));
    }
}
