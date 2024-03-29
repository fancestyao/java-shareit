package ru.practicum.shareit.booking.services.interfaces;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut getBooking(Long userId, Long bookingId);

    BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId);

    List<BookingDtoOut> getAllOwnerBookings(Long userID, String state, Long from, Long size);

    List<BookingDtoOut> getAllBookings(Long userId, String state, Long from, Long size);

    BookingDtoOut setApproval(Long ownerId, Long bookingId, Boolean approve);
}
