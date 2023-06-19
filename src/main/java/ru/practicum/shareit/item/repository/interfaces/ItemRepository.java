package ru.practicum.shareit.item.repository.interfaces;

import ru.practicum.shareit.item.models.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item getItemById(Long itemId);

    Item updateItem(Item item);

    List<Item> getListOfUserItems(Long userId);

    List<Item> findItem(String text);
}
