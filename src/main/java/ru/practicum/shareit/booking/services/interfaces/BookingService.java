package ru.practicum.shareit.booking.services.interfaces;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBooking(Long userId, Long bookingId);

    Booking createBooking(BookingDtoIn bookingDtoIn, Long userId);

    List<Booking> getAllOwnerBookings(Long userID, String state);

    List<Booking> getAllBookings(Long userId, String state);

    BookingDtoOut setApproval(Long ownerId, Long bookingId, Boolean approve);
}
