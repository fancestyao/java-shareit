package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.client.BookingClient;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.enums.Status.WAITING;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BookingClient.class)
public class BookingClientTest {
    @Autowired
    private BookingClient bookingClient;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void setApproved() throws JsonProcessingException {
        boolean approved = true;
        BookingDtoIn booking = new BookingDtoIn();
        booking.setStatus(WAITING);
        String response = objectMapper.writeValueAsString(booking);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.bookingClient.setApproval(1L, 1L, approved);
        Assertions.assertNotNull(objectResponseEntity);
        Assertions.assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
    }

    @Test
    void createBooking() throws JsonProcessingException {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        String requestJson = objectMapper.writeValueAsString(
                bookingInputDto);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(requestJson, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = this.bookingClient.createBooking(bookingInputDto, 1L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getBooking() throws JsonProcessingException {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        String response = objectMapper.writeValueAsString(bookingInputDto);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = this.bookingClient.getBooking(1L, 1L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getAllBookings() throws JsonProcessingException {
        BookingDtoIn bookingInputDto1 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        BookingDtoIn bookingInputDto2 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        String string = objectMapper.writeValueAsString(List.of(bookingInputDto1, bookingInputDto2));
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=3"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = this.bookingClient.getAllBookings(1L, "ALL", 0L, 3L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object bookings = responseEntity.getBody();
        Assertions.assertNotNull(bookings);
    }

    @Test
    void getAllOwnerBookings() throws JsonProcessingException {
        BookingDtoIn bookingInputDto1 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        BookingDtoIn bookingInputDto2 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(WAITING)
                .build();
        String string = objectMapper.writeValueAsString(List.of(bookingInputDto1, bookingInputDto2));
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL&from=0&size=3"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = this.bookingClient.getAllOwnerBookings(1L, "ALL", 0L, 3L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object bookings = responseEntity.getBody();
        Assertions.assertNotNull(bookings);
    }
}