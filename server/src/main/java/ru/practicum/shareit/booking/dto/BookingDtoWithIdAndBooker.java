package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingDtoWithIdAndBooker {
    private Long id;
    private Long bookerId;
}
