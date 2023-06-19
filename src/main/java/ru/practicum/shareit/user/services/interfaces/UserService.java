package ru.practicum.shareit.user.services.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(long id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDTO);

    UserDto updateUser(long userId, UserDto userDTO);

    void removeUser(long userId);
}
