package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.SameEmailException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user3 = new User(3, "Sanya", "sanya@mail.ru");
        user2 = new User(2, "Vlad", "vlad@mail.ru");
        user1 = new User(1, "Nikita", "nikita@mail.ru");
    }

    @Test
    public void getUsersGoodTest() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getUsers().stream().map(UserMapper::toUser).collect(Collectors.toList());
        assertNotNull(result);
        assertEquals(3, result.size());
        assertArrayEquals(users.toArray(), result.toArray());
    }

    @Test
    public void getUserByIdGoodTest() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        User result = UserMapper.toUser(userService.getUserById(userId));
        assertEquals(user1, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void deleteUserGoodTest() {
        long userId = 1;
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);

    }

    @Test
    public void addUserGoodTest() {
        when(userRepository.save(user1)).thenReturn(user1);
        User result = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(user1)));
        assertEquals(user1, result);
        verify(userRepository, times(1)).save(user1);

    }

    @Test
    public void addUserWithSameEmailBadTest() {
        when(userRepository.save(user1)).thenThrow(new SameEmailException("user with this email already created"));
        assertThrows(SameEmailException.class, () -> userService.addUser(UserMapper.toUserDto(user1)));
        verify(userRepository, times(1)).save(user1);

    }

    @Test
    public void updateUserGoodTest() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);
        user2.setId(0);
        userService.updateUser(UserMapper.toUserDto(user2), userId);
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getEmail(), user2.getEmail());
    }

    @Test
    public void updateUserWithSameEmailBadTest() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenThrow(new SameEmailException("user with this email already created"));
        user2.setId(0);
        assertThrows(SameEmailException.class, () -> userService.updateUser(UserMapper.toUserDto(user2), userId));
    }
}
