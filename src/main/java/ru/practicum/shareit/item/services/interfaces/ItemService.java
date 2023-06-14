package ru.practicum.shareit.item.services.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDTO);

    List<ItemDto> getUserItems(Long userId);

    ItemDto updateItem(Long userID, ItemDto item, Long itemId);

    ItemDto getItem(Long itemId);

    List<ItemDto> searchItem(String text);
}
