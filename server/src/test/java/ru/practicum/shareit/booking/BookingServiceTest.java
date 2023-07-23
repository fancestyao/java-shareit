package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.services.interfaces.BookingService;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void beforeEach() {
        userOne = new User();
        userOne.setName("userOneName");
        userOne.setEmail("userOneEmail@mail.ru");
        userTwo = new User();
        userTwo.setName("userTwoName");
        userTwo.setEmail("userTwoEmail@mail.ru");
        itemOne = new Item(null, userOne, "testItem", "testDescription", true, null);
        itemTwo = new Item(null, userOne, "testItem2", "testDescription2", true, null);
    }

    @Test
    void createBookingByUserTest() {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(Status.APPROVED)
                .build();
        userRepository.save(userOne);
        userRepository.save(userTwo);
        itemRepository.save(itemOne);
        Assertions.assertEquals(itemOne, bookingService.createBooking(bookingInputDto, 2L).getItem());
    }

    @Test
    void getBooking() {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(Status.APPROVED)
                .build();
        userRepository.save(userOne);
        userRepository.save(userTwo);
        itemRepository.save(itemOne);
        bookingService.createBooking(bookingInputDto, 2L);
        Assertions.assertEquals(itemOne, bookingService.getBooking(2L, 1L).getItem());
    }

    @Test
    void setApprovedTest() {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(Status.WAITING)
                .build();
        userRepository.save(userOne);
        userRepository.save(userTwo);
        itemRepository.save(itemOne);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproval(userOne.getId(), 1L, true);
        System.out.println(bookingService.getBooking(2L, 1L).getStatus());
        Assertions.assertEquals(Status.APPROVED, bookingService.getBooking(2L, 1L).getStatus());
    }

    @Test
    void getAllBookingsTest() {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(Status.WAITING)
                .build();
        BookingDtoIn bookingInputDto2 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(2L)
                .status(Status.WAITING)
                .build();
        userRepository.save(userOne);
        userRepository.save(userTwo);
        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproval(userOne.getId(), 1L, true);
        bookingService.createBooking(bookingInputDto2, 2L);
        bookingService.setApproval(userOne.getId(), 2L, true);
        List<BookingDtoOut> allBookings = bookingService.getAllBookings(2L, "ALL", 0L, 4L);
        allBookings.sort((b, b2) -> Math.toIntExact(b.getId() - b2.getId()));
        Assertions.assertAll(() -> Assertions.assertEquals(itemOne, allBookings.get(0).getItem()),
                () -> Assertions.assertEquals(itemTwo, allBookings.get(1).getItem()));
    }

    @Test
    void getAllOwnerBookingsTest() {
        BookingDtoIn bookingInputDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(Status.WAITING)
                .build();
        BookingDtoIn bookingInputDto2 = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(2L)
                .status(Status.WAITING)
                .build();
        userRepository.save(userOne);
        userRepository.save(userTwo);
        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproval(userOne.getId(), 1L, true);
        bookingService.createBooking(bookingInputDto2, 2L);
        bookingService.setApproval(userOne.getId(), 2L, true);
        List<BookingDtoOut> allBookings = bookingService.getAllOwnerBookings(1L, "ALL", 0L, 4L);
        allBookings.sort((b, b2) -> Math.toIntExact(b.getId() - b2.getId()));
        Assertions.assertAll(() -> Assertions.assertEquals(itemOne, allBookings.get(0).getItem()),
                () -> Assertions.assertEquals(itemTwo, allBookings.get(1).getItem()));
    }
}
