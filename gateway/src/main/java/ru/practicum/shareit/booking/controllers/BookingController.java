package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.request.enums.Status.WAITING;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private static final String CUSTOM_USER_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(CUSTOM_USER_HEADER) Long bookerId,
                                                @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        bookingDtoIn.setStatus(WAITING);
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())
                || bookingDtoIn.getStart().equals(bookingDtoIn.getEnd())) {
            log.warn("Окончание бронирования не может быть раньше начала.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Окончание бронирования не может быть раньше начала.");
        }
        ResponseEntity<Object> booking = bookingClient.createBooking(bookingDtoIn, bookerId);
        log.info("Запрос на сохранение букинга {}.", bookingDtoIn);
        return booking;
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestHeader(CUSTOM_USER_HEADER) Long ownerId,
                                             @PathVariable Long bookingId,
                                             @RequestParam boolean approved) {
        log.info("Запрос на подтверждение букинга с id {}.", bookingId);
        return bookingClient.setApproval(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(CUSTOM_USER_HEADER) Long userID,
                                             @PathVariable Long bookingId) {
        log.info("Запрос на получение букинга с id {}.", bookingId);
        return bookingClient.getBooking(userID, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(CUSTOM_USER_HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                                     @Positive @RequestParam(defaultValue = "10") Long size) {
        log.info("Запрос на получение всех букингов для пользователя с id {}.", userId);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnersBookings(@RequestHeader(CUSTOM_USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                                       @Positive @RequestParam(defaultValue = "10") Long size) {
        log.info("Запрос на получение всех букингов для обладателя с id {}.", userId);
        return bookingClient.getAllOwnerBookings(userId, state, from, size);
    }
}
