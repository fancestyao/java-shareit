package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created = LocalDateTime.now();
}
