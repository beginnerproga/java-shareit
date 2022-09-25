package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
   private final HashMap<Long, User> users = new HashMap<>();
   private long counts = 1;

    @Override
    public User addUser(User user) {
        user.setId(counts);
        users.put(counts, user);
        counts++;
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user, long userId) {
        if (user.getName() != null)
            users.get(userId).setName(user.getName());
        if (user.getEmail() != null)
            users.get(userId).setEmail(user.getEmail());
        return users.get(userId);
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
