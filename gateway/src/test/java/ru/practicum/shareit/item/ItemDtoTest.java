package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerializeToJson() throws IOException {
        ItemDto dto = new ItemDto(1L,
                "itemDtoName",
                "itemDtoDescription",
                Boolean.TRUE,
                2L);
        JsonContent<ItemDto> result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemDtoName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("itemDtoDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(Boolean.TRUE);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @Test
    void testDeserializeFromJson() throws IOException {
        String jsonString = "{\"id\": 1, \"name\": \"itemDtoName\", \"description\": \"itemDtoDescription\"," +
                " \"available\": true, \"requestId\": 2}";
        ItemDto dto = json.parseObject(jsonString);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("itemDtoName");
        assertThat(dto.getDescription()).isEqualTo("itemDtoDescription");
        assertThat(dto.getAvailable()).isEqualTo(Boolean.TRUE);
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }
}