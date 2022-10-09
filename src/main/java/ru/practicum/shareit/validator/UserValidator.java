package ru.practicum.shareit.validator;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public class UserValidator {
    public static boolean validateForEmail(List<User> users, User user) {
        return users.stream().anyMatch(x -> x.getEmail().equals(user.getEmail()));
    }
}
