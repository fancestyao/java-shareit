package ru.practicum.shareit.request.models;

import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestedData {
    @NotNull
    private final ItemRequestInputDto itemRequestInputDto;
    @NotNull
    private final Long creatorId;
}
