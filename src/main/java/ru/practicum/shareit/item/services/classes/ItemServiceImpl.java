package ru.practicum.shareit.item.services.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.interfaces.ItemRepository;
import ru.practicum.shareit.item.services.interfaces.ItemService;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.interfaces.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDTO) {
        Item newItem = itemMapper.fromDTO(itemDTO);
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        } else {
            newItem.setOwner(userId);
        }
        Item createdItem = itemRepository.createItem(newItem);
        return itemMapper.toDTO(createdItem);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.getListOfUserItems(userId).stream().toList().stream().filter(i -> Objects.equals(i.getOwner(), userId)).map(itemMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDTO, Long itemId) {
        Item item = itemMapper.fromDTO(itemDTO);
        userIdValidation(userId);
        Item oldItem = itemRepository.getItemById(itemId);
        itemOwnerValidation(item, oldItem, userId);
        itemValidation(item, oldItem, userId);
        Item updatedItem = itemRepository.updateItem(oldItem);
        return itemMapper.toDTO(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item itemDTO = itemRepository.getItemById(itemId);
        return itemMapper.toDTO(itemDTO);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> list = itemRepository.findItem(text);
        return itemMapper.listToDTO(list);
    }

    private void itemValidation(Item item, Item oldItem, Long userId) {
        if (!oldItem.getOwner().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем.");
        } else if (item.getName() != null) {
            oldItem.setName(item.getName());
        } else if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        } else if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void userIdValidation(Long userId) {
        if (!userRepository.getAllUsers().contains(userRepository.getUser(userId))) {
            throw new NotFoundException("Пользователя нет в системе.");
        }
    }

    private void itemOwnerValidation(Item item, Item oldItem, Long userId) {
        if (!oldItem.getOwner().equals(userId)) {
            throw new NotFoundException("Данный пользователь не владеет этой вещью.");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }
}
