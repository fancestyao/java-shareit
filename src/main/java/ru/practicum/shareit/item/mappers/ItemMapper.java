package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.models.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDTO(Item item);

    List<ItemDto> listToDTO(List<Item> items);

    Item fromDTO(ItemDto userDTO);
}
