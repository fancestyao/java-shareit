package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.models.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDtoToItem(ItemDto itemDTO, long userId);

    ArrayList<ItemDto> itemsToDTOItems(Collection<Item> itemDtos);

    ItemDto itemToItemDTO(Item item);

    ItemDtoWithBooking itemToItemDTOWithBookings(Item item, List<CommentDto> comments);

    List<ItemDtoWithBooking> itemsToItemsDtoWithBookings(List<Item> items);
}
