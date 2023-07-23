package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String CUSTOMER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        ResponseEntity<Object> result = itemClient.getUserItems(userId);
        log.info("Получен запрос от пользователя {} на получение его вещей.", userId);
        return result;
    }

    @PostMapping
    public ResponseEntity<Object>  createItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        ResponseEntity<Object> result = itemClient.createItem(userId, itemDto);
        log.info("Получен запрос от пользователя {} на добавление вещи.", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object>  getItem(@PathVariable Long itemId,
                                      @RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        ResponseEntity<Object> result = itemClient.getItem(userId, itemId);
        log.info("Получен запрос от пользователя {} на получение вещи {}.", userId, itemId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object>  updateItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        ResponseEntity<Object> result = itemClient.updateItem(userId, itemDto, itemId);
        log.info("Получен запрос от пользователя {} на обновление вещи {}.", userId, itemId);
        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object>  searchItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                    @RequestParam(name = "text") String text) {
        ResponseEntity<Object> result = itemClient.searchItem(text, userId);
        log.info("Получен запрос от пользователя {} на поиск предмета по описанию.", userId);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object>  addComment(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDtoInput commentDto) {
        ResponseEntity<Object> result = itemClient.createComment(userId, itemId, commentDto);
        log.info("Получен запрос от пользователя {} на добавление комментария к вещи {}.", userId, itemId);
        return result;
    }
}