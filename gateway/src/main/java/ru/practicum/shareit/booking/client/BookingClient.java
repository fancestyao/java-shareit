package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.client.WebClient;

import java.util.Map;

@Service
public class BookingClient extends WebClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> setApproval(Long ownerId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> createBooking(BookingDtoIn bookingDtoIn, Long bookerId) {
        return post("", bookerId, bookingDtoIn);
    }

    public ResponseEntity<Object> getBooking(Long userID, Long bookingId) {
        return get("/" + bookingId, userID);
    }

    public ResponseEntity<Object> getAllBookings(Long userID, String state, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userID, parameters);
    }

    public ResponseEntity<Object> getAllOwnerBookings(Long userID, String state, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userID, parameters);
    }
}
