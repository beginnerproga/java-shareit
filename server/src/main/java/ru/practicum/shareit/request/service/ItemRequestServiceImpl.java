package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)

public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public ItemRequestInfoDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Received request to add ItemRequest with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestInfoDto(itemRequest);
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequests(Long userId) {
        log.info("Received request to get all ItemRequests with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        List<ItemRequest> itemRequests = itemRequestRepository.findByUserOrderByCreatedDesc(user);
        List<ItemRequestInfoDto> itemRequestInfoDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findByItemRequestOrderById(itemRequest);
            itemRequestInfoDtos.add(ItemRequestMapper.toItemRequestInfoDto(itemRequest, items));
        }
        return itemRequestInfoDtos;
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequestsPagination(Long userId, Integer from, Integer size) {
        log.info("Received request to get all ItemRequests from user's id={} using pagination", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        int page = from / size;
        List<ItemRequestInfoDto> itemRequestInfoDtos = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findOrderByCreatedDesc(userId, PageRequest.of(page, size)).getContent();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findByItemRequestOrderById(itemRequest);
            itemRequestInfoDtos.add(ItemRequestMapper.toItemRequestInfoDto(itemRequest, items));
        }
        return itemRequestInfoDtos;
    }

    @Override
    public ItemRequestInfoDto getItemRequestById(long requestId, Long userId) {
        log.info("Received request to get ItemRequest with id = ", requestId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            throw new ItemRequestNotFoundException("Item request with id = " + requestId + " not found");

        });
        List<Item> items = itemRepository.findByItemRequestOrderById(itemRequest);
        return ItemRequestMapper.toItemRequestInfoDto(itemRequest, items);
    }
}
