package ru.practicum.shareit.request.models;

import lombok.Data;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
