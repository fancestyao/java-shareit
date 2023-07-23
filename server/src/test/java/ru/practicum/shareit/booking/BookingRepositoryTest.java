package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest(properties = {"spring.config.name=application-test", "spring.config.location=classpath:application-test.properties"})
@ExtendWith(SpringExtension.class)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;

    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private Booking booking;

    @BeforeEach
    void bBeforeEach() {
        userOne = new User();
        userOne.setName("testUser");
        userOne.setEmail("test@email.ru");
        userTwo = new User();
        userTwo.setName("TestBooker");
        userTwo.setEmail("testBooker@email.ru");
        itemOne = new Item(null, userOne, "testItem", "testDescription", true, null);
        itemTwo = new Item(null, userOne, "testItem3", "testDescription3", true, null);
        booking = new Booking();
        booking.setBooker(userTwo);
        booking.setStatus(Status.WAITING);
        booking.setItem(itemTwo);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(25));
    }

    @Test
    void findByIdWithItemAndBooker() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithItemAndBooker(booking.getId(), userTwo.getId());
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get());
    }

    @Test
    void findAllCurrentByUserIdAndSortByDesc() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userTwo.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllCurrentByOwnerIdAndSortByDesc() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(userOne.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllPastByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(booking.getStart().plusHours(10));
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByBookerIdAndEndBefore(userTwo.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllPastByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(booking.getStart().plusHours(10));
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByOwnerIdAndEndBefore(userOne.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllFutureByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByBookerIdAndStartAfter(userTwo.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllFutureByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());
        Optional<List<Booking>> optionalBooking = bookingRepository.findAllByOwnerIdAndStartAfter(userOne.getId(), LocalDateTime.now(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findBookingsByItemId() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemTwo);
        entityManager.persist(booking);
        Optional<List<Booking>> optionalBooking = Optional.ofNullable(bookingRepository.findBookingsByItemId(List.of(itemTwo.getId())));
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findBookingByBookerIdAndItemIdAndStatusApproved() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(4));
        booking.setStatus(Status.APPROVED);
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemTwo);
        entityManager.persist(booking);
        Optional<List<Booking>> optionalBooking = Optional.ofNullable(bookingRepository.findBookingByBookerIdAndItemIdAndStatusApproved(userTwo.getId(), itemTwo.getId()));
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }
}