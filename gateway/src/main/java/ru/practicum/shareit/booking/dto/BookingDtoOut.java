package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.enums.Status;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoOut {
    private Long id;
    private ItemDto item;
    private UserDto booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}

