package ru.practicum.shareit.request.service.interfaces;

import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(ItemRequestedData inputDto);

    List<RequestDtoWithItems> getRequests(Long creatorId);

    List<RequestDtoWithItems> getRequestsInPages(Long creatorId, Long from, Long size);

    RequestDtoWithItems getRequest(Long creatorId, Long itemRequest);
}