package ru.practicum.shareit.user.services.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.interfaces.UserRepository;
import ru.practicum.shareit.user.services.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDTO) {
        if (userDTO.getEmail() == null) {
            throw new ValidationException("Почта не может быть пустой.");
        } else if (!userDTO.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат почты.");
        }
        User user = repository.createUser(mapper.fromDTO(userDTO));
        return mapper.toDTO(user);
    }

    @Override
    public UserDto getUser(long id) {
        return mapper.toDTO(repository.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDTO) {
        User user = mapper.fromDTO(userDTO);
        return mapper.toDTO(repository.updateUser(userId, user));
    }

    @Override
    public void removeUser(long userId) {
        repository.deleteUser(userId);
    }
}
