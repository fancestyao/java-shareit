package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.request.service.interfaces.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private static final String CUSTOM_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    Request createRequest(@RequestHeader(CUSTOM_ID_HEADER) Long creatorId,
                          @RequestBody @Valid ItemRequestInputDto inputDto) {
        ItemRequestedData itemRequestData = new ItemRequestedData(inputDto, creatorId);
        return requestService.createRequest(itemRequestData);
    }

    @GetMapping
    List<RequestDtoWithItems> getRequests(@RequestHeader(CUSTOM_ID_HEADER) Long creatorId) {
        return requestService.getRequests(creatorId);
    }

    @GetMapping("/all")
    List<RequestDtoWithItems> getRequestsInPages(@RequestHeader(CUSTOM_ID_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "0") Long from,
                                                 @RequestParam(defaultValue = "10") Long size
    ) {
        if (from < 0) {
            throw new BadRequestException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new BadRequestException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
        return requestService.getRequestsInPages(userId, from, size);
    }

    @GetMapping("{requestId}")
    RequestDtoWithItems getRequest(@RequestHeader(CUSTOM_ID_HEADER) Long userId,
                                   @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}
