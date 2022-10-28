package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.SameEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        user3 = new UserDto(3, "Sanya", "sanya@mail.ru");
        user2 = new UserDto(2, "Vlad", "vlad@mail.ru");
        user1 = new UserDto(1, "Nikita", "nikita@mail.ru");
    }

    @Test
    public void getUsersGoodTest() throws Exception {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(user1);
        userDtos.add(user2);
        userDtos.add(user3);
        when(userService.getUsers()).thenReturn(userDtos);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDtos.get(0).getName()), String.class))
                .andExpect(jsonPath("$.[0].email", is(userDtos.get(0).getEmail()), String.class))
                .andExpect(jsonPath("$.[1].id", is(userDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(userDtos.get(1).getName()), String.class))
                .andExpect(jsonPath("$.[1].email", is(userDtos.get(1).getEmail()), String.class))
                .andExpect(jsonPath("$.[2].id", is(userDtos.get(2).getId()), Long.class))
                .andExpect(jsonPath("$.[2].name", is(userDtos.get(2).getName()), String.class))
                .andExpect(jsonPath("$.[2].email", is(userDtos.get(2).getEmail()), String.class));
        verify(userService, times(1)).getUsers();

    }

    @Test
    public void getUserByIdGoodTest() throws Exception {
        Long userId1 = user1.getId();
        when(userService.getUserById(userId1)).thenReturn(user1);
        mvc.perform(get("/users/{userId}", userId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user1.getEmail()), String.class));
        verify(userService, times(1)).getUserById(userId1);

        Long userId2 = user2.getId();
        when(userService.getUserById(userId2)).thenReturn(user2);
        mvc.perform(get("/users/{userId}", userId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user2.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user2.getEmail()), String.class));
        verify(userService, times(1)).getUserById(userId2);

    }

    @Test
    public void getUserByIncorrectIdBadTest() throws Exception {
        long incorrectId = 10;
        when(userService.getUserById(incorrectId))
                .thenThrow(new UserNotFoundException("User with id=" + incorrectId + " not found"));
        mvc.perform(get("/users/{userId}", incorrectId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id=" + incorrectId + " not found"), String.class));

    }

    @Test
    public void addUserGoodTest() throws Exception {
        user1.setId(0);
        when(userService.addUser(user1))
                .thenReturn(user1);
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user1.getEmail()), String.class));

        verify(userService, times(1)).addUser(user1);

    }

    @Test
    public void addUserWithNullNameBadTest() throws Exception {
        user1.setName(null);
        user1.setId(0);
        when(userService.addUser(user1))
                .thenReturn(user1);
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).addUser(user1);

    }

    @Test
    public void addUserWithNotBlankNameBadTest() throws Exception {
        user1.setName("");
        user1.setId(0);
        when(userService.addUser(user1))
                .thenReturn(user1);
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).addUser(user1);

    }

    @Test
    public void addUserWithNullEmailBadTest() throws Exception {
        user1.setEmail(null);
        user1.setId(0);
        when(userService.addUser(user1))
                .thenReturn(user1);
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).addUser(user1);

    }

    @Test
    public void addUserWithSameEmailBadTest() throws Exception {
        user1.setId(0);
        when(userService.addUser(user1)).thenThrow(new SameEmailException("this email created"));
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("this email created")));

        verify(userService, times(1)).addUser(user1);

    }

    @Test
    public void addUserWithIncorrectEmailBadTest() throws Exception {
        user1.setEmail("23incorrect");
        user1.setId(0);
        when(userService.addUser(user1))
                .thenReturn(user1);
        user1.setId(1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).addUser(user1);

    }

    @Test
    public void updateUserGoodTest() throws Exception {
        UserDto updateUser = new UserDto("Update Nikita", "updateNikita@email.ru");
        Long userId = user1.getId();
        when(userService.updateUser(updateUser, userId))
                .thenReturn(new UserDto(1, updateUser.getName(), updateUser.getEmail()));
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(updateUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(updateUser.getEmail()), String.class));

        verify(userService, times(1)).updateUser(updateUser, userId);

    }

    @Test
    public void updateUserWithSameEmailBadTest() throws Exception {
        UserDto updateUser = new UserDto("Update Nikita", "updateNikita@email.ru");
        Long userId = user1.getId();
        when(userService.updateUser(updateUser, userId))
                .thenThrow(new SameEmailException("this email created"));
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("this email created")));
        verify(userService, times(1)).updateUser(updateUser, userId);

    }

    @Test
    public void deleteUserGoodTest() throws Exception {
        Long userId = user1.getId();
        doNothing().when(userService).deleteUser(userId);
        mvc.perform(delete("/users/{userId}", userId));
        verify(userService, times(1)).deleteUser(userId);

    }

}

