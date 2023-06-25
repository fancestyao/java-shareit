package ru.practicum.shareit.request.service.interfaces;

import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(ItemRequestedData itemRequestedData);

    List<RequestDtoWithItems> getRequestsInPages(Long creatorId, Long from, Long size);

    List<RequestDtoWithItems> getRequests(Long creatorId);

    RequestDtoWithItems getRequestById(Long creatorId, Long itemRequest);
}
