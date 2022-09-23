package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.SameEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.validator.UserValidator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Received request to get all users");
        return userDao.getUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Received request to get a user by userId={}", userId);
        User result = userDao.getUserById(userId);
        if (result == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        return UserMapper.toUserDto(result);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Received request to delete a user by userId={}", userId);
        User result = userDao.getUserById(userId);
        if (result == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        userDao.deleteUser(userId);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("Received request to add user");
        User user = UserMapper.toUser(userDto);
        if (UserValidator.validateForEmail(userDao.getUsers(), user))
            throw new SameEmailException("Same email created");
        userDao.addUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Received request to update user");
        User check = userDao.getUserById(userId);
        if (check == null)
            throw new UserNotFoundException("User with id=" + userId + " not found");
        if (userDto.getEmail() != null && UserValidator.validateForEmail(userDao.getUsers(), UserMapper.toUser(userDto)))
            throw new SameEmailException("Same email created");
        User result = userDao.updateUser(UserMapper.toUser(userDto), userId);
        return UserMapper.toUserDto(result);
    }
}
