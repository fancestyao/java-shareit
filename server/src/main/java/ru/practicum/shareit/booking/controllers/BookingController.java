package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.services.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.models.Status;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private static final String CUSTOM_USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader(CUSTOM_USER_HEADER) Long bookerId,
                             @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        bookingDtoIn.setStatus(Status.WAITING);
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())
                || bookingDtoIn.getStart().equals(bookingDtoIn.getEnd())) {
            log.warn("Окончание бронирования не может быть раньше начала.");
            throw new BadRequestException("Окончание бронирования не может быть раньше начала.");
        }
        log.info("Запрос на сохранение букинга {}.", bookingDtoIn);
        return bookingService.createBooking(bookingDtoIn, bookerId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOut setApprove(@RequestHeader(CUSTOM_USER_HEADER) Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam boolean approved) {
        log.info("Запрос на подтверждение букинга с id {}.", bookingId);
        return bookingService.setApproval(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOut getBooking(@RequestHeader(CUSTOM_USER_HEADER) Long userID,
                                    @PathVariable Long bookingId) {
        log.info("Запрос на получение букинга с id {}.", bookingId);
        return bookingService.getBooking(userID, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllUserBookings(@RequestHeader(CUSTOM_USER_HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero       @RequestParam(defaultValue = "0") Long from,
                                                  @Positive @RequestParam(defaultValue = "10") Long size) {
        log.info("Запрос на получение всех букингов для пользователя с id {}.", userId);
        return bookingService.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwnersBookings(@RequestHeader(CUSTOM_USER_HEADER) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                                    @Positive @RequestParam(defaultValue = "10") Long size) {
        log.info("Запрос на получение всех букингов для обладателя с id {}.", userId);
        return bookingService.getAllOwnerBookings(userId, state, from, size);
    }
}
