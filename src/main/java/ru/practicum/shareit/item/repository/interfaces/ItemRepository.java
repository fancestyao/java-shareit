package ru.practicum.shareit.item.repository.interfaces;

import ru.practicum.shareit.item.models.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item getItemById(long itemId);

    Item updateItem(Item item);

    List<Item> getListOfUserItems(long userId);

    List<Item> findItem(String text);
}
