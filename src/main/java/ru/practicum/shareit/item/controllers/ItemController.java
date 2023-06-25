package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
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
public class ItemController {
    private static final String CUSTOMER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> getUserItems(@RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        List<ItemDtoWithBooking> result = itemService.getUserItems(userId);
        System.out.println(result);
        return result;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        ItemDto result = itemService.createItem(userId, itemDto);
        System.out.println(result);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@PathVariable Long itemId,
                                      @RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        ItemDtoWithBooking result = itemService.getItem(userId, itemId);
        System.out.println(result);
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
        System.out.println(result);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                    @RequestParam(name = "text") String text) {
        List<ItemDto> result = itemService.searchItem(text);
        System.out.println(result);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDtoInput commentDto) {
        CommentDto result = itemService.createComment(userId, itemId, commentDto);
        System.out.println(result);
        return result;
    }
}