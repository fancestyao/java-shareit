package ru.practicum.shareit.booking.services.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.services.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.interfaces.ItemRepository;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public Booking createBooking(BookingDtoIn bookingDtoIn, Long userId) {
        Optional<Item> item = itemRepository.findById(bookingDtoIn.getItemId());

        if (!item.isPresent()) {
            throw new NotFoundException("Вещи не существует.");
        }

        if (item.get().getUser().getId().equals(userId)) {
            throw new NotFoundException("Вы не можете забронировать свою вещь.");
        }
        if (!item.get().getAvailable()) {
            throw new BadRequestException("Вещь уже забронирована.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь посылающий запрос не найден.");
        }
        Booking booking = bookingMapper.fromDto(bookingDtoIn);
        booking.setBooker(user.get());
        booking.setItem(item.get());
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllOwnerBookings(Long userId, String state) {
        Optional<List<Booking>> bookingList;
        Optional<User> user = userRepository.findById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByOwnerId(userId, sort);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByOwnerIdAndEndBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByOwnerIdAndStartAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByOwnerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByOwnerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (!bookingList.isPresent()) {
            throw new NotFoundException("Бронирования не найдены");
        }
        System.out.println(bookingList);
        return bookingList.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long userId, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithItemAndBooker(bookingId, userId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException("Такой брони еще нет.");
        }
        return optionalBooking.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings(Long userId, String state) {
        Optional<List<Booking>> bookingList;
        Optional<User> user = userRepository.findById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (!bookingList.isPresent()) {
            throw new NotFoundException("Бронирования не найдены");
        }
        System.out.println(bookingList);
        return bookingList.get();
    }

    @Override
    @Transactional
    public BookingDtoOut setApproval(Long userId, Long bookingId, Boolean isApproved) {
        Optional<Booking> optionalBooking = bookingRepository.findBookingByIdAndItemUserId(bookingId, userId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException("Такого бронирования не существует.");
        }
        Booking booking = optionalBooking.get();
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже одобрено.");
        }

        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        System.out.println(booking);
        return bookingMapper.toDto(booking);
    }
}
