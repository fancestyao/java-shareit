package ru.practicum.shareit.user.repository.interfaces;

import ru.practicum.shareit.user.models.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUser(long userId);

    User updateUser(long userId, User user);

    void deleteUser(long userId);

    List<User> getAllUsers();
}
