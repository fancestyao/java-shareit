package ru.practicum.shareit.user.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDTO(User user);

    User fromDTO(UserDto userDTO);
}
