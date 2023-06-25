package ru.practicum.shareit.user.services.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.interfaces.UserRepository;
import ru.practicum.shareit.user.services.interfaces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDTO) {
        User user = repository.save(mapper.fromDTO(userDTO));
        return mapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (!optionalUser.isPresent())
            throw new NotFoundException("Пользователь не найден.");
        User user = optionalUser.get();
        return mapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> allUsers = repository.findAll();
        return allUsers
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDTO) {
        User user = mapper.fromDTO(userDTO);
        Optional<User> userDb = repository.findById(userId);
        if (!userDb.isPresent())
            throw new NotFoundException("Пользователь не найден.");
        if (user.getEmail() != null && user.getName() != null) {
            userDb.get().setId(userId);
            userDb.get().setName(user.getName());
            userDb.get().setEmail(user.getEmail());
        } else if (user.getName() != null) {
            userDb.get().setName(user.getName());
        } else if (user.getEmail() != null) {
            userDb.get().setEmail(user.getEmail());
        }
        User userToSave = userDb.get();
        repository.save(userToSave);
        return mapper.toDTO(userToSave);
    }

    @Override
    @Transactional
    public void removeUser(long userId) {
        repository.deleteById(userId);
    }
}
