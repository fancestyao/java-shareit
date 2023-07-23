package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private Status status;
}