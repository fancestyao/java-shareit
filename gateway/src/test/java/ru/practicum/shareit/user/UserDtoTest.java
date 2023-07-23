package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userWithValidEmailAndName() throws IOException {
        UserDto userDTO = new UserDto(1L, "jsonUserEmail@mail.ru", "jsonUserName");
        JsonContent<UserDto> result = json.write(userDTO);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("jsonUserEmail@mail.ru");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("jsonUserName");
    }
}