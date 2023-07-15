package ru.practicum.shareit.request.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.models.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request inputDtoToRequest(ItemRequestInputDto itemRequestInputDto);

    RequestDtoWithItems requestToRequestDtoWithItems(Request request);
}