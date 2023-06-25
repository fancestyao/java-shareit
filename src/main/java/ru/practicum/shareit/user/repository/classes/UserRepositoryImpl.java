package ru.practicum.shareit.user.repository.classes;

import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl {
    private final HashMap<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private Long id = 1L;

    public User createUser(User user) {
        long userId = id;
        emailValidation(userId, user.getEmail());
        id++;
        user.setId(userId);
        users.put(userId, user);
        emails.put(userId, user.getEmail());
        return user;
    }

    public User getUser(long userId) {
        if (users.containsKey(userId)) return users.get(userId);
        throw new NotFoundException("Пользователь не найден.");
    }

    public User updateUser(long userId, User user) {
        emailValidation(userId, user.getEmail());
        if (user.getEmail() != null && user.getName() != null) {
            user.setId(userId);
            users.put(userId, user);
            emails.put(userId, user.getEmail());
        } else if (user.getEmail() == null && user.getName() != null) {
            users.get(userId).setName(user.getName());
        } else if (user.getEmail() != null) {
            users.get(userId).setEmail(user.getEmail());
            emails.put(userId, user.getEmail());
        } else {
            throw new NotFoundException("Пользователь не найден.");
        }
        return users.get(userId);
    }

    public void deleteUser(long userId) {
        emails.remove(userId);
        users.remove(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void emailValidation(long userId, String email) {
        if (emails.containsValue(email) && !emails.get(userId).equals(email))
            throw new EmailValidationException("Данная электронная почта уже используется.");
    }
}