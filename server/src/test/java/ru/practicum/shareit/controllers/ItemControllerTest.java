package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void getItemsGoodTest() throws Exception {
        List<ItemInfoDto> itemInfoDtos = new ArrayList<>();
        itemInfoDtos.add(new ItemInfoDto(1L, "item", "description", true));
        itemInfoDtos.add(new ItemInfoDto(2L, "item2", "123", true));
        long userId = 1;
        when(itemService.getItems(userId, 0, 2)).thenReturn(itemInfoDtos);
        mvc.perform(get("/items").header("X-Sharer-User-Id", userId).queryParam("from", "0").queryParam("size", "2")).andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", is(itemInfoDtos.get(0).getId()), Long.class)).andExpect(jsonPath("$.[0].name", is(itemInfoDtos.get(0).getName()), String.class)).andExpect(jsonPath("$.[0].description", is(itemInfoDtos.get(0).getDescription()), String.class)).andExpect(jsonPath("$.[0].available", is(itemInfoDtos.get(0).getAvailable()), Boolean.class)).andExpect(jsonPath("$.[1].id", is(itemInfoDtos.get(1).getId()), Long.class)).andExpect(jsonPath("$.[1].name", is(itemInfoDtos.get(1).getName()), String.class)).andExpect(jsonPath("$.[1].description", is(itemInfoDtos.get(1).getDescription()), String.class)).andExpect(jsonPath("$.[1].available", is(itemInfoDtos.get(1).getAvailable()), Boolean.class));
        verify(itemService, times(1)).getItems(userId, 0, 2);
    }

    @Test
    public void getItemsWithNullUserIdBadTest() throws Exception {
        Long userId = null;
        when(itemService.getItems(userId, 0, 2)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(get("/items").queryParam("from", "0").queryParam("size", "2")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemService, times(1)).getItems(userId, 0, 2);
    }

    @Test
    public void getItemsWithNotFoundUserIdBadTest() throws Exception {
        long userId = 3232;
        when(itemService.getItems(userId, 0, 2)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));
        mvc.perform(get("/items").header("X-Sharer-User-Id", userId).queryParam("from", "0").queryParam("size", "2")).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemService, times(1)).getItems(userId, 0, 2);
    }

    @Test
    public void getItemByIdGoodTest() throws Exception {
        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "item", "description", true);
        long userId = 1;
        when(itemService.getItemById(itemInfoDto.getId(), userId)).thenReturn(itemInfoDto);
        mvc.perform(get("/items/{itemId}", itemInfoDto.getId()).header("X-Sharer-User-Id", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemInfoDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemInfoDto.getDescription()), String.class)).andExpect(jsonPath("$.available", is(itemInfoDto.getAvailable()), Boolean.class));

        verify(itemService, times(1)).getItemById(itemInfoDto.getId(), userId);

    }

    @Test
    public void getItemByIdWithNullUserIdBadTest() throws Exception {
        long itemId = 1;
        Long userId = null;
        when(itemService.getItemById(itemId, userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(get("/items/{itemId}", itemId)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemService, times(1)).getItemById(itemId, userId);

    }

    @Test
    public void getItemByIdWitNotFoundUserIdBadTest() throws Exception {
        long itemId = 1;
        long userId = 32323;
        when(itemService.getItemById(itemId, userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));
        mvc.perform(get("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemService, times(1)).getItemById(itemId, userId);

    }

    @Test
    public void getItemByIdWitNotFoundItemIdIdBadTest() throws Exception {
        long userId = 1;
        long itemId = 32323;
        when(itemService.getItemById(itemId, userId)).thenThrow(new ItemNotFoundException("Item with id=" + itemId + " not found"));

        mvc.perform(get("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("Item with id=" + itemId + " not found")));

        verify(itemService, times(1)).getItemById(itemId, userId);

    }

    @Test
    public void addItemGoodTest() throws Exception {
        ItemDto itemDto = new ItemDto("item", "description", true);
        itemDto.setId(1);
        long userId = 1;
        when(itemService.addItem(itemDto, userId)).thenReturn(itemDto);
        mvc.perform(post("/items").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class)).andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1)).addItem(itemDto, userId);

    }

    @Test
    public void addCommentGoodTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "good thing");
        CommentInfoDto commentInfoDto = new CommentInfoDto(1L, "text", "Artem", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        long itemId = 1;
        long userId = 1;
        when(itemService.addComment(itemId, userId, commentDto)).thenReturn(commentInfoDto);
        mvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(commentInfoDto.getId()), Long.class)).andExpect(jsonPath("$.text", is(commentInfoDto.getText()), String.class)).andExpect(jsonPath("$.authorName", is(commentInfoDto.getAuthorName()), String.class)).andExpect(jsonPath("$.created", is(commentInfoDto.getCreated().toString()), String.class));

        verify(itemService, times(1)).addComment(itemId, userId, commentDto);
    }

    @Test
    public void addCommentWithNullUserIdBadTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "good thing");
        long itemId = 1;
        Long userId = null;
        when(itemService.addComment(itemId, userId, commentDto)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));
        mvc.perform(post("/items/{itemId}/comment", itemId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemService, times(1)).addComment(itemId, userId, commentDto);
    }

    @Test
    public void addCommentWithNotFoundUserIdBadTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "good thing");
        long itemId = 1;
        long userId = 2323;
        when(itemService.addComment(itemId, userId, commentDto)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));

        mvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemService, times(1)).addComment(itemId, userId, commentDto);
    }

    @Test
    public void addCommentWithNotFoundItemIdBadTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "good thing");
        long itemId = 2331;
        long userId = 1;
        when(itemService.addComment(itemId, userId, commentDto)).thenThrow(new ItemNotFoundException("Item with id=" + itemId + " not found"));

        mvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("Item with id=" + itemId + " not found")));

        verify(itemService, times(1)).addComment(itemId, userId, commentDto);
    }

    @Test
    public void addCommentWithUserNotBookingBadTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "good thing");
        long itemId = 1;
        long userId = 5;
        when(itemService.addComment(itemId, userId, commentDto)).thenThrow(new CommentException("User cannot to add comment, because he doesn't book"));

        mvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User cannot to add comment, because he doesn't book")));

        verify(itemService, times(1)).addComment(itemId, userId, commentDto);
    }


    @Test
    public void updateItemGoodTest() throws Exception {
        long itemId = 1;
        long userId = 1;
        ItemDto itemDto = new ItemDto("item", "description", true);
        when(itemService.updateItem(itemDto, itemId, userId)).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class)).andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        verify(itemService, times(1)).updateItem(itemDto, itemId, userId);
    }

    @Test
    public void updateItemWithNullUserIdBadTest() throws Exception {
        long itemId = 1;
        Long userId = null;
        ItemDto itemDto = new ItemDto("item", "description", true);
        when(itemService.updateItem(itemDto, itemId, userId)).thenThrow(new UserIdWasNotTransferredException("User's id is null"));

        mvc.perform(patch("/items/{itemId}", itemId).content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is("User's id is null")));

        verify(itemService, times(1)).updateItem(itemDto, itemId, userId);
    }

    @Test
    public void updateItemWithNotFoundUserIdBadTest() throws Exception {
        long itemId = 1;
        long userId = 2323;
        ItemDto itemDto = new ItemDto("item", "description", true);
        when(itemService.updateItem(itemDto, itemId, userId)).thenThrow(new UserNotFoundException("User with id=" + userId + " not found"));

        mvc.perform(patch("/items/{itemId}", itemId).content(mapper.writeValueAsString(itemDto)).header("X-Sharer-User-Id", userId).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " not found")));

        verify(itemService, times(1)).updateItem(itemDto, itemId, userId);
    }

    @Test
    public void updateItemWithUserNotAccessBadTest() throws Exception {
        long itemId = 1;
        long userId = 5;
        ItemDto itemDto = new ItemDto("item", "description", true);
        when(itemService.updateItem(itemDto, itemId, userId)).thenThrow(new UserNotHaveAccessException("User with id=" + userId + " doesn't have access"));

        mvc.perform(patch("/items/{itemId}", itemId).content(mapper.writeValueAsString(itemDto)).header("X-Sharer-User-Id", userId).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("User with id=" + userId + " doesn't have access")));

        verify(itemService, times(1)).updateItem(itemDto, itemId, userId);

    }

}
