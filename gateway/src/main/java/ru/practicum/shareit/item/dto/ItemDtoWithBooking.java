package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoWithIdAndBooker;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoWithIdAndBooker lastBooking;
    private BookingDtoWithIdAndBooker nextBooking;
    private LocalDateTime lastBookingStart;
    private LocalDateTime lastBookingEnd;
    private LocalDateTime nextBookingStart;
    private LocalDateTime nextBookingEnd;
    private List<CommentDto> comments;
}