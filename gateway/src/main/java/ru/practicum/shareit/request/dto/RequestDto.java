package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class RequestDto {
    @NotBlank
    @Size(max = 512)
    private String description;
}
