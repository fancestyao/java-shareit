package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        ResponseEntity<Object> response = userClient.getUsers();
        log.info("Получен запрос на получение всех пользователей.");
        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        ResponseEntity<Object> response = userClient.getUser(userId);
        log.info("Получен запрос на получение пользователя с id {}.", userId);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        ResponseEntity<Object> response = userClient.createUser(userDto);
        log.info("Получен запрос на создание пользователя {}.", userDto);
        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);
        log.info("Получен запрос на обновление пользователя с id {}.", userId);
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id {}.", userId);
        userClient.deleteUser(userId);
    }
}
