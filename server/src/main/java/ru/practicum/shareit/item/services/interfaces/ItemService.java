package ru.practicum.shareit.item.services.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    List<ItemDtoWithBooking> getUserItems(Long userId);

    ItemDto updateItem(Long userId, ItemDto item);

    ItemDtoWithBooking getItem(Long userId, Long itemId);

    List<ItemDto> searchItem(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDtoInput commentDtoInput);
}
