package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemToRequestDto;

import java.io.IOException;

@JsonTest
class ItemToRequestDtoTest {
    @Autowired
    private JacksonTester<ItemToRequestDto> json;

    @Test
    void itemRequestInputDtoTest() throws IOException {
        ItemToRequestDto inputDto = new ItemToRequestDto(1L,
                "itemToRequestDtoName",
                "itemToRequestDtoDescription",
                null,
                1L);
        JsonContent<ItemToRequestDto> result = json.write(inputDto);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("itemToRequestDtoDescription");
    }
}