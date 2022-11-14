package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserIdWasNotTransferredException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)

public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void addItemRequestGoodTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "werwe");
        long userId = 1;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(itemRequestDto.getId(), itemRequestDto.getDescription(), LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS), null);
        when(itemRequestService.addItemRequest(itemRequestDto, userId)).thenReturn(itemRequestInfoDto);
        mvc.perform(post("/requests").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemRequestDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemRequestInfoDto.getId()), Long.class)).andExpect(jsonPath("$.created", is(itemRequestInfoDto.getCreated().toString()), String.class)).andExpect(jsonPath("$.description", is(itemRequestInfoDto.getDescription()), String.class)).andExpect(jsonPath("$.items", is(itemRequestInfoDto.getItems()), List.class));

        verify(itemRequestService, times(1)).addItemRequest(itemRequestDto, userId);

    }

    @Test
    public void addItemRequestWithNullUserIdBadTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "werwe");
        Long userId = null;
        when(itemRequestService.addItemRequest(itemRequestDto, userId))
                .thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemRequestService, times(1)).addItemRequest(itemRequestDto, userId);

    }

    @Test
    public void addItemRequestWithNotFoundUserIdBadTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "werwe");
        long userId = 2345;
        when(itemRequestService.addItemRequest(itemRequestDto, userId))
                .thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));
        mvc.perform(post("/requests").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemRequestDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemRequestService, times(1)).addItemRequest(itemRequestDto, userId);

    }

    @Test
    public void getItemRequestsGoodTest() throws Exception {
        long userId = 1;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(1, "fefe", LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS), null);
        List<ItemRequestInfoDto> itemRequestInfoDtos = new ArrayList<>();
        itemRequestInfoDtos.add(itemRequestInfoDto);
        when(itemRequestService.getItemRequests(userId)).thenReturn(itemRequestInfoDtos);

        mvc.perform(get("/requests").header("X-Sharer-User-Id", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", is(itemRequestInfoDto.getId()), Long.class)).andExpect(jsonPath("$.[0].created", is(itemRequestInfoDto.getCreated().toString()), String.class)).andExpect(jsonPath("$.[0].description", is(itemRequestInfoDto.getDescription()), String.class)).andExpect(jsonPath("$.[0].items", is(itemRequestInfoDto.getItems()), List.class));

        verify(itemRequestService, times(1)).getItemRequests(userId);

    }

    @Test
    public void getItemRequestsWithNullUserIdBadTest() throws Exception {
        Long userId = null;
        when(itemRequestService.getItemRequests(userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));

        mvc.perform(get("/requests")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemRequestService, times(1)).getItemRequests(userId);

    }

    @Test
    public void getItemRequestsWithNotFoundUserIdBadTest() throws Exception {
        long userId = 232;
        when(itemRequestService.getItemRequests(userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));

        mvc.perform(get("/requests").header("X-Sharer-User-Id", userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemRequestService, times(1)).getItemRequests(userId);

    }


    @Test
    public void getItemRequestsPaginationGoodTest() throws Exception {
        long userId = 1;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(1, "fefe", LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS), null);
        List<ItemRequestInfoDto> itemRequestInfoDtos = new ArrayList<>();
        itemRequestInfoDtos.add(itemRequestInfoDto);
        when(itemRequestService.getItemRequestsPagination(userId, 0, 2)).thenReturn(itemRequestInfoDtos);

        mvc.perform(get("/requests/all").queryParam("from", "0").queryParam("size", "2").header("X-Sharer-User-Id", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", is(itemRequestInfoDto.getId()), Long.class)).andExpect(jsonPath("$.[0].created", is(itemRequestInfoDto.getCreated().toString()), String.class)).andExpect(jsonPath("$.[0].description", is(itemRequestInfoDto.getDescription()), String.class)).andExpect(jsonPath("$.[0].items", is(itemRequestInfoDto.getItems()), List.class));

        verify(itemRequestService, times(1)).getItemRequestsPagination(userId, 0, 2);

    }

    @Test
    public void getItemRequestsPaginationWithNullUserIdBadTest() throws Exception {
        Long userId = null;
        when(itemRequestService.getItemRequestsPagination(userId, 0, 2)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));

        mvc.perform(get("/requests/all").queryParam("from", "0").queryParam("size", "2")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemRequestService, times(1)).getItemRequestsPagination(userId, 0, 2);

    }

    @Test
    public void getItemRequestsPaginationWithNotFoundUserIdBadTest() throws Exception {
        long userId = 1212;
        when(itemRequestService.getItemRequestsPagination(userId, 0, 2)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", userId).queryParam("from", "0").queryParam("size", "2")).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemRequestService, times(1)).getItemRequestsPagination(userId, 0, 2);

    }

    @Test
    public void getItemRequestByIdGoodTest() throws Exception {
        long userId = 1;
        long requestId = 1;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(1, "fefe", LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS), null);
        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(itemRequestInfoDto);

        mvc.perform(get("/requests/{requestId}", requestId).header("X-Sharer-User-Id", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemRequestInfoDto.getId()), Long.class)).andExpect(jsonPath("$.created", is(itemRequestInfoDto.getCreated().toString()), String.class)).andExpect(jsonPath("$.description", is(itemRequestInfoDto.getDescription()), String.class)).andExpect(jsonPath("$.items", is(itemRequestInfoDto.getItems()), List.class));

        verify(itemRequestService, times(1)).getItemRequestById(requestId, userId);

    }

    @Test
    public void getItemRequestByIdWithNullUserIdBadTest() throws Exception {
        Long userId = null;
        long requestId = 1;
        when(itemRequestService.getItemRequestById(requestId, userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));

        mvc.perform(get("/requests/{requestId}", requestId)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemRequestService, times(1)).getItemRequestById(requestId, userId);

    }

    @Test
    public void getItemRequestByIdWithNotFoundUserIdBadTest() throws Exception {
        long userId = 3232;
        long requestId = 1;
        when(itemRequestService.getItemRequestById(requestId, userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));

        mvc.perform(get("/requests/{requestId}", requestId).header("X-Sharer-User-Id", userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemRequestService, times(1)).getItemRequestById(requestId, userId);

    }

    @Test
    public void getItemRequestByIdWithNotFoundRequestIdBadTest() throws Exception {
        long userId = 3;
        long requestId = 1232;
        when(itemRequestService.getItemRequestById(requestId, userId)).thenThrow(new ItemRequestNotFoundException("Item request with id = " + requestId + " not found"));

        mvc.perform(get("/requests/{requestId}", requestId).header("X-Sharer-User-Id", userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("Item request with id = " + requestId + " not found")));

        verify(itemRequestService, times(1)).getItemRequestById(requestId, userId);

    }


}
