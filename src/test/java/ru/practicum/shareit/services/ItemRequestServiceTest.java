package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private User user2;
    private Item item1;
    private ItemRequest itemRequest;
    private List<ItemRequest> itemRequests;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        User user1 = new User(1, "Nikita", "nikita@mail.ru");
        user2 = new User(2, "Qrew", "re@mail.ru");
        item1 = new Item(1, "qerq", "qwrqeqw", true, user1);
        itemRequest = new ItemRequest(1, "fwefw", user2, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
    }

    @Test
    public void addItemRequestGoodTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription());
        long userId = 2;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        ItemRequestInfoDto result = itemRequestService.addItemRequest(itemRequestDto, userId);
        result.setCreated(result.getCreated().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(ItemRequestMapper.toItemRequestInfoDto(itemRequest, new ArrayList<>()), result);
        verify(userRepository,times(1)).findById(userId);
        verify(itemRequestRepository,times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void getItemRequestGoodTest() {
        item1.setItemRequest(itemRequest);
        long userId = 2;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findByUserOrderByCreatedDesc(user2)).thenReturn(itemRequests);
        when(itemRepository.findByItemRequestOrderById(itemRequest)).thenReturn(Collections.singletonList(item1));
        List<ItemRequestInfoDto> itemRequestInfoDto = itemRequestService.getItemRequests(userId);
        assertEquals(1, itemRequestInfoDto.size());
        ItemRequestInfoDto expected = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        expected.setItems(Collections.singletonList(new ItemRequestInfoDto.ItemForRequest(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getItemRequest().getId())));
        assertEquals(expected, itemRequestInfoDto.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findByUserOrderByCreatedDesc(user2);
        verify(itemRepository, times(1)).findByItemRequestOrderById(itemRequest);

    }

    @Test
    public void getItemRequestsPaginationGoodTest() {
        int from = 0;
        int size = 10;
        int page = 0;
        item1.setItemRequest(itemRequest);
        long userId = 2;
        final PageImpl<ItemRequest> pageItemsRequest = new PageImpl<>(itemRequests);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findOrderByCreatedDesc(user2.getId(), PageRequest.of(page, size))).thenReturn(pageItemsRequest);
        when(itemRepository.findByItemRequestOrderById(itemRequest)).thenReturn(Collections.singletonList(item1));
        List<ItemRequestInfoDto> itemRequestInfoDto = itemRequestService.getItemRequestsPagination(userId, from, size);
        assertEquals(1, itemRequestInfoDto.size());
        ItemRequestInfoDto expected = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        expected.setItems(Collections.singletonList(new ItemRequestInfoDto.ItemForRequest(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getItemRequest().getId())));
        assertEquals(expected, itemRequestInfoDto.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findOrderByCreatedDesc(user2.getId(), PageRequest.of(page, size));
        verify(itemRepository, times(1)).findByItemRequestOrderById(itemRequest);


    }

    @Test
    public void getItemRequestByIdGoodTest() {
        item1.setItemRequest(itemRequest);
        long userId = 2;
        long itemRequestId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByItemRequestOrderById(itemRequest)).thenReturn(Collections.singletonList(item1));
        ItemRequestInfoDto result = itemRequestService.getItemRequestById(itemRequestId, userId);
        ItemRequestInfoDto expected = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        expected.setItems(Collections.singletonList(new ItemRequestInfoDto.ItemForRequest(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getItemRequest().getId())));
        assertEquals(expected, result);
        verify(userRepository,times(1)).findById(userId);
        verify(itemRequestRepository,times(1)).findById(itemRequestId);
        verify(itemRepository,times(1)).findByItemRequestOrderById(itemRequest);
    }
}
