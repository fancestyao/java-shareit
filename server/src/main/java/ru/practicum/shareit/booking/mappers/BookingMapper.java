package ru.practicum.shareit.booking.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoWithIdAndBooker;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.Collection;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "item.id", source = "itemId")
    Booking fromDto(BookingDtoIn bookingDtoIn);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoWithIdAndBooker toDtoShortVersion(Booking booking);

    ArrayList<BookingDtoOut> toDtoBookings(Collection<Booking> bookingsDto);

    BookingDtoOut toDto(Booking booking);
}
