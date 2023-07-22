package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDtoInput;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoInTest {
    @Autowired
    private JacksonTester<CommentDtoInput> json;

    @Test
    void serializeFromDtoToJson() throws IOException {
        CommentDtoInput dto = new CommentDtoInput("commentDtoInputText");
        JsonContent<CommentDtoInput> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("commentDtoInputText");
    }

    @Test
    void deserializeFromJsonToDto() throws IOException {
        String jsonString = "{\"text\":\"commentDtoInputText\"}";
        CommentDtoInput dto = json.parseObject(jsonString);

        assertThat(dto.getText()).isEqualTo("commentDtoInputText");
    }
}