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

import java.util.ArrayList;
import java.util.List;
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
            newItem.setUser(user);
        }
        Item createdItem = itemRepository.createItem(newItem);
        return itemMapper.toDTO(createdItem);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return new ArrayList<>(itemRepository.getListOfUserItems(userId))
                .stream()
                .map(itemMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDTO, Long itemId) {
        Item item = itemMapper.fromDTO(itemDTO);
        Item oldItem = itemRepository.getItemById(itemId);
        itemOwnerValidation(item, oldItem, userId);
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

    private void itemOwnerValidation(Item item, Item oldItem, Long userId) {
        if (!oldItem.getUser().getId().equals(userId)) {
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
