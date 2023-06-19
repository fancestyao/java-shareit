package ru.practicum.shareit.item.repository.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.interfaces.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsOfUser = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        itemsOfUser.computeIfAbsent(item.getUser().getId(), i -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId))
            return items.get(itemId);
        return null;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        itemsOfUser.computeIfAbsent(item.getUser().getId(), i -> new ArrayList<>()).remove(item);
        itemsOfUser.computeIfAbsent(item.getUser().getId(), i -> new ArrayList<>()).add(item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> getListOfUserItems(Long userId) {
        return itemsOfUser.get(userId);
    }

    @Override
    public List<Item> findItem(String text) {
        List<Item> foundItems = new ArrayList<>();
        items.values().forEach(item -> {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) &&
                    item.getAvailable() && !text.equals("")
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable() && !text.equals("")) {
                foundItems.add(item);
            }
        });
        return foundItems;
    }
}
