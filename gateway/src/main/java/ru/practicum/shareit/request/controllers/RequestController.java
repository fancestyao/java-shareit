package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private static final String CUSTOM_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(CUSTOM_ID_HEADER) Long creatorId,
                                                @RequestBody @Valid ItemRequestInputDto inputDto) {
        ResponseEntity<Object> request = requestClient.addRequest(inputDto, creatorId);
        log.info("Запрос на сохранение запроса предмета {}.", inputDto);
        return request;
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(CUSTOM_ID_HEADER) Long userId) {
        ResponseEntity<Object> request = requestClient.getRequests(userId);
        log.info("Запрос на получение запросов на предметы пользователя с id {}.", userId);
        return request;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsInPages(@RequestHeader(CUSTOM_ID_HEADER) Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                                     @Positive @RequestParam(defaultValue = "10") Long size) {
        ResponseEntity<Object> request = requestClient.getRequestInPages(userId, from, size);
        log.info("Запрос на получение запросов на предметы пользователя с id {}.", userId);
        return request;
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(CUSTOM_ID_HEADER) Long userId,
                                             @PathVariable Long requestId) {
        ResponseEntity<Object> request = requestClient.getRequest(userId, requestId);
        log.info("Запрос на получение запроса с id {} пользователя с id {}.", requestId, userId);
        return request;
    }
}
