package ru.practicum.shareit.request.service.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemToRequestDto;
import ru.practicum.shareit.item.repository.interfaces.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.interfaces.RequestService;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public Request createRequest(ItemRequestedData itemRequestedData) {
        User user = userRepository.findById(itemRequestedData.getCreatorId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Request request = requestMapper.toRequest(itemRequestedData.getItemRequestInputDto());

        request.setRequester(user);
        requestRepository.save(request);

        return request;
    }

    @Override
    public List<RequestDtoWithItems> getRequestsInPages(Long creatorId, Long from, Long size) {
        List<RequestDtoWithItems> result = new ArrayList<>();

        userValidation(creatorId);

        PageRequest sortedByCreated = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue(), Sort.by("created").ascending());
        result = getItemRequestDtoWithItems(creatorId, result, sortedByCreated, false);

        return result;
    }

    @Override
    public List<RequestDtoWithItems> getRequests(Long creatorId) {
        return null;
    }

    @Override
    public RequestDtoWithItems getRequestById(Long creatorId, Long itemRequest) {
        List<RequestDtoWithItems> result = new ArrayList<>();

        requestValidation(creatorId, itemRequest);

        Optional<Request> requestedItem = requestRepository.findById(itemRequest);

        if (requestedItem.isPresent()) {
            result = getItemRequestDtoWithItems(Map.of(itemRequest, requestedItem.get()));
        }
        return result.get(0);
    }

    private List<RequestDtoWithItems> getItemRequestDtoWithItems(Long userId, List<RequestDtoWithItems> result,
                                                                 Pageable sortedByCreation,
                                                                 Boolean isCreator) {
        Map<Long, Request> userRequests;

        if (isCreator) {
            userRequests = requestRepository.findAllByRequesterIdOrderByCreatedAsc(userId, sortedByCreation).stream().collect(Collectors.toMap(Request::getId, itemRequest -> itemRequest));
        } else {
            userRequests = requestRepository.findAllByRequesterIdNot(userId, sortedByCreation).stream().collect(Collectors.toMap(Request::getId, itemRequest -> itemRequest));
        }

        if (!userRequests.isEmpty()) {
            result = getItemRequestDtoWithItems(userRequests);
        }

        return result;
    }

    private List<RequestDtoWithItems> getItemRequestDtoWithItems(Map<Long, Request> itemRequests) {
        List<RequestDtoWithItems> result = new ArrayList<>();

        RequestDtoWithItems requestWithItems;

        List<Long> requestsId = new ArrayList<>(itemRequests.keySet());
        Map<Long, ItemToRequestDto> responseItems = itemRepository.findAllByRequests(requestsId).stream().collect(Collectors.toMap(ItemToRequestDto::getRequestId, item -> item));

        for (Long requestId : requestsId) {
            requestWithItems = requestMapper.requestDtoWithItems(itemRequests.get(requestId));
            List<ItemToRequestDto> items = responseItems.values().stream().filter(item -> item.getRequestId().equals(requestId)).collect(Collectors.toList());
            requestWithItems.setItemsToRequest(items);
            result.add(requestWithItems);
        }
        return result;
    }

    private void userValidation(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void requestValidation(Long creatorId, Long itemRequestId) {
        if (!userRepository.existsById(creatorId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!requestRepository.existsById(itemRequestId)) {
            throw new NotFoundException("Реквест не найден");
        }
    }
}
