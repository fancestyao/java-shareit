package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public List<ItemDto> getUserItems(@RequestHeader(CUSTOMER_ID_HEADER) Long userId) {
        return itemService.getUserItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDTO) {
        return itemService.createItem(userId, itemDTO);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CUSTOMER_ID_HEADER) Long userId,
                              @RequestBody ItemDto item,
                              @PathVariable Long itemId) {
        return itemService.updateItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        return itemService.searchItem(text);
    }
}