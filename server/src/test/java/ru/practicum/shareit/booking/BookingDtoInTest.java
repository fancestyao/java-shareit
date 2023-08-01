package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.request.models.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoInTest {
    @Autowired
    private JacksonTester<BookingDtoIn> json;

    @Test
    void testSerializeToJson() throws IOException {
        LocalDateTime start = LocalDateTime.of(2023, 6, 10, 15, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 10, 16, 0, 0);
        Long itemId = 1L;
        Status status = Status.WAITING;
        BookingDtoIn dto = BookingDtoIn.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .status(status)
                .build();
        JsonContent<BookingDtoIn> result = json.write(dto);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-10T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-10T16:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
