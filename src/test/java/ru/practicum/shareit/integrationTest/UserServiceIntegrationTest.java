package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    private final UserService userService;

    @Test
    public void addUserGoodTest() {
        User user = new User(1, "a", "a@mail.ru");
        assertEquals(UserMapper.toUserDto(user), userService.addUser(UserMapper.toUserDto(user)));
    }

    @Test
    public void getUsersGoodTest() {
        User user = new User(1, "a", "a@mail.ru");
        assertEquals(UserMapper.toUserDto(user), userService.addUser(UserMapper.toUserDto(user)));
        List<UserDto> userDtoList = userService.getUsers();
        assertEquals(1, userDtoList.size());
    }


    @Test
    public void getUserBadTest() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(66L));
    }

    @Test
    public void updateUserGoodTest() {
        User user = new User(1, "a", "a@mail.ru");
        userService.addUser(UserMapper.toUserDto(user));
        User user1 = new User(2, "b", "b@mail.ru");
        user1.setId(1);
        assertEquals(UserMapper.toUserDto(user1), userService.updateUser(UserMapper.toUserDto(user1), 1L));
    }

}
