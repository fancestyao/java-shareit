package ru.practicum.shareit.request.service.classes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemToRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.interfaces.RequestService;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Transactional
    @Override
    public Request createRequest(ItemRequestedData itemRequestData) {
        User user = userRepository.findById(itemRequestData.getCreatorId()).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + itemRequestData.getCreatorId() + " не найден"));
        Request itemRequest = requestMapper.inputDtoToRequest(itemRequestData.getInputDto());
        itemRequest.setRequester(user);
        itemRequest.setCreated(itemRequestData.getInputDto().getCreated());
        requestRepository.save(itemRequest);
        log.info("Добавлено в базу данных: " + itemRequest);
        return itemRequest;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDtoWithItems> getRequests(Long creatorId) {
        userValidation(creatorId);
        List<RequestDtoWithItems> result = new ArrayList<>();
        Long userRequestCount = requestRepository.countAllByRequesterId(creatorId);
        if (userRequestCount == 0) {
            userRequestCount++;
        }
        Pageable sortedByCreated = PageRequest.of(0, userRequestCount.intValue(), Sort.by("created")
                .ascending());
        result = getItemRequestDtoWithItems(creatorId, result, sortedByCreated, true);
        log.info("Список полученных запросов: " + result);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDtoWithItems> getRequestsInPages(Long userId, Long from, Long size) {
        userValidation(userId);
        List<RequestDtoWithItems> result = new ArrayList<>();
        PageRequest sortedByCreated = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0,
                size.intValue(), Sort.by("created").ascending());
        result = getItemRequestDtoWithItems(userId, result, sortedByCreated, false);
        log.info("Список полученных запросов: " + result);
        return result;
    }

    private List<RequestDtoWithItems> getItemRequestDtoWithItems(Long userId,
                                                                     List<RequestDtoWithItems> result,
                                                                     Pageable sortedByCreated,
                                                                     boolean isCreator) {
        Map<Long, Request> userRequests;
        if (isCreator) {
            userRequests = requestRepository.findAllByRequesterIdOrderByCreatedAsc(userId, sortedByCreated).stream()
                    .collect(Collectors.toMap(Request::getId, itemRequest -> itemRequest));
        } else {
            userRequests = requestRepository.findAllByRequesterIdNot(userId, sortedByCreated).stream()
                    .collect(Collectors.toMap(Request::getId, itemRequest -> itemRequest));
        }
        if (!userRequests.isEmpty()) {
            result = getItemRequestDtoWithItems(userRequests);
        }
        log.info("Список полученных запросов: " + result);
        return result;
    }

    private List<RequestDtoWithItems> getItemRequestDtoWithItems(Map<Long, Request> itemRequests) {
        List<RequestDtoWithItems> result = new ArrayList<>();
        RequestDtoWithItems requestWithItems;
        List<Long> requestsId = new ArrayList<>(itemRequests.keySet());
        Map<Long, ItemToRequestDto> responseItems = itemRepository.findAllByRequests(requestsId)
                .stream()
                .collect(Collectors.toMap(ItemToRequestDto::getRequestId, item -> item));
        for (Long requestId : requestsId) {
            requestWithItems = requestMapper.requestToRequestDtoWithItems(itemRequests.get(requestId));
            List<ItemToRequestDto> items = responseItems.values()
                    .stream()
                    .filter(item -> item.getRequestId().equals(requestId))
                    .collect(Collectors.toList());
            requestWithItems.setItems(items);
            result.add(requestWithItems);
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public RequestDtoWithItems getRequest(Long creatorId, Long itemRequestId) {
        validation(creatorId, itemRequestId);
        List<RequestDtoWithItems> result = new ArrayList<>();
        Optional<Request> itemRequest = requestRepository.findById(itemRequestId);
        if (itemRequest.isPresent()) {
            result = getItemRequestDtoWithItems(Map.of(itemRequestId, itemRequest.get()));
        }
        log.info("Получен запрос: " + result.get(0));
        return result.get(0);
    }

    private void userValidation(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }

    private void validation(Long creatorId, Long itemRequestId) {
        if (!userRepository.existsById(creatorId)) {
            log.warn("Пользователь с id: " + creatorId + " не найден");
            throw new NotFoundException("Пользователь с id: " + creatorId + " не найден");
        }
        if (!requestRepository.existsById(itemRequestId)) {
            log.warn("Запрос с id: " + itemRequestId + " не найден");
            throw new NotFoundException("Запрос с id: " + itemRequestId + " не найден");
        }
    }
}