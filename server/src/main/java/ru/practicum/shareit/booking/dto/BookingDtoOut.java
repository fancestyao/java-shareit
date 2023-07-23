package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoOut {
    private Long id;
    private Item item;
    private User booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}
