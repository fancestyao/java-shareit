package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.services.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.models.Status;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {
    private static final String CUSTOMER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@RequestHeader(CUSTOMER_ID_HEADER) Long bookerId,
                       @RequestBody @Valid BookingDtoIn bookingInputDto) {
        bookingInputDto.setStatus(Status.WAITING);
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())
                || bookingInputDto.getStart().equals(bookingInputDto.getEnd())) {
            throw new BadRequestException("Окончание бронирования не может быть раньше начала.");
        }
        log.info("Букинг добавлен.");
        return bookingService.createBooking(bookingInputDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut setApprove(@RequestHeader(CUSTOMER_ID_HEADER) Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("Букинг одобрен.");
        return bookingService.setApproval(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBooking(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                              @PathVariable Long bookingId) {
        log.info("Букинг получен.");
        return bookingService.getBooking(userID, bookingId);
    }

    @GetMapping()
    public List<BookingDtoOut> getAllUserBookings(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Все букинги пользователя получены.");
        return bookingService.getAllBookings(userID, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwnersBookings(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Все пользователи букингов получены.");
        return bookingService.getAllOwnerBookings(userID, state);
    }
}
