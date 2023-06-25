package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.services.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.models.Status;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private static final String CUSTOMER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public Booking add(@RequestHeader(CUSTOMER_ID_HEADER) Long bookerId,
                       @RequestBody @Valid BookingDtoIn bookingInputDto) {
        bookingInputDto.setStatus(Status.WAITING);
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())
                || bookingInputDto.getStart().equals(bookingInputDto.getEnd())) {
            throw new BadRequestException("Окончание бронирования не может быть раньше начала.");
        }
        return bookingService.createBooking(bookingInputDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut setApprove(@RequestHeader(CUSTOMER_ID_HEADER) Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.setApproval(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                              @PathVariable Long bookingId) {
        return bookingService.getBooking(userID, bookingId);
    }

    @GetMapping()
    public List<Booking> getAllUserBookings(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookings(userID, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllOwnersBookings(@RequestHeader(CUSTOMER_ID_HEADER) Long userID,
                                              @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerBookings(userID, state);
    }
}
