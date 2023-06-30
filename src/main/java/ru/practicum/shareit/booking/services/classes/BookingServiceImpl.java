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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId) {
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                                  .orElseThrow(() -> new NotFoundException("Вещи не существует."));

        if (item.getUser().getId().equals(userId)) {
            throw new NotFoundException("Вы не можете забронировать свою вещь.");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь уже забронирована.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь посылающий запрос не найден.");
        }
        Booking booking = bookingMapper.fromDto(bookingDtoIn);
        booking.setBooker(user.get());
        booking.setItem(item);
        //System.out.println(booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllOwnerBookings(Long userId, String state) {
        Optional<List<Booking>> bookingList;
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

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
        //System.out.println(bookingList);
        return bookingMapper.toDtoBookings(bookingList.get());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdWithItemAndBooker(bookingId, userId)
                                           .orElseThrow(() -> new NotFoundException("Такой брони еще нет."));
        //System.out.println(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookings(Long userId, String state) {
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
        //System.out.println(bookingList);
        return bookingMapper.toDtoBookings(bookingList.get());
    }

    @Override
    @Transactional
    public BookingDtoOut setApproval(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository
                          .findBookingByIdAndItemUserId(bookingId, userId)
                          .orElseThrow(() -> new NotFoundException("Такого бронирования не существует."));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже одобрено.");
        }

        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        //System.out.println(booking);
        return bookingMapper.toDto(booking);
    }
}
