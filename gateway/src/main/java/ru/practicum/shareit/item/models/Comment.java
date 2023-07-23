package ru.practicum.shareit.item.models;

import lombok.*;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}
