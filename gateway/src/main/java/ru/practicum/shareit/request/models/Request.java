package ru.practicum.shareit.request.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Request {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}