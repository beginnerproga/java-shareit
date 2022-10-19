package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.SameEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Received request to get all users");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Received request to get a user by userId={}", userId);
        User result = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        return UserMapper.toUserDto(result);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Received request to delete a user by userId={}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("Received request to add user");
        User user = UserMapper.toUser(userDto);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new SameEmailException("user with this email already created");
        }
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Received request to update user");
        User check = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        User result = UserMapper.toUser(userDto);
        if (result.getEmail() != null && !result.getEmail().isBlank())
            check.setEmail(result.getEmail());
        if (result.getName() != null && !result.getName().isBlank())
            check.setName(result.getName());
        try {
            userRepository.save(check);
            return UserMapper.toUserDto(check);
        } catch (Exception e) {
            throw new SameEmailException("user with this email already created");
        }
    }
}
