package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.services.interfaces.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String CUSTOMER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> getUserItems(@RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        List<ItemDtoWithBooking> result = itemService.getUserItems(userId);
        log.info("Получен запрос от пользователя {} на получение его вещей.", userId);
        return result;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        ItemDto result = itemService.createItem(userId, itemDto);
        log.info("Получен запрос от пользователя {} на добавление вещи.", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@PathVariable Long itemId,
                                      @RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        ItemDtoWithBooking result = itemService.getItem(userId, itemId);
        log.info("Получен запрос от пользователя {} на получение вещи {}.", userId, itemId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        ItemDto result = itemService.updateItem(userId, itemDto);
        log.info("Получен запрос от пользователя {} на обновление вещи {}.", userId, itemId);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                    @RequestParam(name = "text") String text) {
        List<ItemDto> result = itemService.searchItem(text);
        log.info("Получен запрос от пользователя {} на поиск предмета по описанию.", userId);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDtoInput commentDto) {
        CommentDto result = itemService.createComment(userId, itemId, commentDto);
        log.info("Получен запрос от пользователя {} на добавление комментария к вещи {}.", userId, itemId);
        return result;
    }
}