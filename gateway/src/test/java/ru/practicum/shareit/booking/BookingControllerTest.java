package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestClientTest(MockitoExtension.class)
public class BookingControllerTest {
    @InjectMocks
    private BookingController bookingController;
    @Mock
    private BookingClient bookingClient;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private BookingDtoIn bookingInputDto;
    private BookingDtoOut expectedBooking;
    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        Item item = new Item(1L, user, "itemName", "itemDescription", true, null);
        user = new User();
        user.setName("userName");
        user.setEmail("userEmail@mail.ru");
        user.setId(1L);
        bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(10))
                .itemId(item.getId())
                .build();
        expectedBooking = BookingDtoOut.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(10))
                .item(item)
                .build();
    }

    @Test
    void addWithValidInputShouldReturnBooking() throws Exception {
        expectedBooking.setId(1L);
        expectedBooking.setStatus(Status.WAITING);
        Mockito.when(bookingClient.createBooking(Mockito.any(BookingDtoIn.class), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(expectedBooking));
        mockMvc.perform(post("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));

        Mockito.verify(bookingClient).createBooking(Mockito.any(BookingDtoIn.class), Mockito.anyLong());
    }

    @Test
    void addWithInvalidInputShouldReturnBadRequest() {
        expectedBooking.setStart(LocalDateTime.now().plusDays(34));
        Assertions.assertThrows(AssertionError.class,
                () -> mockMvc.perform(post("/bookings")
                                .header(CUSTOM_USER_ID_HEADER, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingInputDto)))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void setApproveWithValidInputShouldReturnBooking() throws Exception {
        expectedBooking.setId(1L);
        Mockito.when(bookingClient.setApproval(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(ResponseEntity.ok(expectedBooking));
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("approved", String.valueOf(Boolean.TRUE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(bookingClient).setApproval(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void getBookingWithValidInputShouldReturnBooking() throws Exception {
        expectedBooking.setId(1L);
        Mockito.when(bookingClient.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(expectedBooking));
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(CUSTOM_USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(bookingClient).getBooking(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getAllUserBookingsWithValidInputShouldReturnListOfBookings() throws Exception {
        List<BookingDtoOut> expectedBookings = Collections.singletonList(expectedBooking);
        Mockito.when(bookingClient.getAllBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(expectedBookings));
        mockMvc.perform(get("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0L))
                        .param("size", String.valueOf(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());
        Mockito.verify(bookingClient).getAllBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getAllOwnersBookingsWithValidInputShouldReturnListOfBookings() throws Exception {
        List<BookingDtoOut> expectedBookings = Collections.singletonList(expectedBooking);
        Mockito.when(bookingClient.getAllOwnerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(expectedBookings));
        mockMvc.perform(get("/bookings/owner")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0L))
                        .param("size", String.valueOf(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());
        Mockito.verify(bookingClient).getAllOwnerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getAllUserBookings_WithInvalidFromValue_ShouldReturnBadRequest() {
        Assertions.assertThrows(java.lang.AssertionError.class, () -> mockMvc.perform(get("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(-1L))
                        .param("size", String.valueOf(10L)))
                .andExpect(status().isBadRequest()));
    }

    @Test
    void getAllUserBookings_WithInvalidSizeValue_ShouldReturnBadRequest() {
        Assertions.assertThrows(java.lang.AssertionError.class, () -> mockMvc.perform(get("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0L))
                        .param("size", String.valueOf(-1L)))
                .andExpect(status().isBadRequest()));
    }
}
