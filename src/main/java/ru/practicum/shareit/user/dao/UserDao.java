package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    List<User> getUsers();

    User getUserById(long id);

    User updateUser(User user, long userId);

    void deleteUser(long userId);
}
