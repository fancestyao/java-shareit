package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.models.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker booker " +
            "WHERE b.id = :bookingId " +
            "AND (booker.id = :userId OR i.user.id = :userId)")
    Optional<Booking> findByIdWithItemAndBooker(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    Optional<Booking> findBookingByIdAndItemUserId(Long bookingId, Long ownerId);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    Optional<List<Booking>> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime, PageRequest sort);

    Optional<List<Booking>> findAllByBookerId(Long bookerId, PageRequest sort);

    Optional<List<Booking>> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime, PageRequest sort);

    Optional<List<Booking>> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime, PageRequest sort);

    Optional<List<Booking>> findAllByBookerIdAndStatus(Long bookerId, Status status, PageRequest sort);

    Optional<List<Booking>> findBookingsByItemIdOrderByEndDesc(Long itemId);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.id in (:itemsId) "
            + "order by b.end desc")
    List<Booking> findBookingsByItemId(@Param("itemsId") List<Long> itemsId);

    @Query("select b "
            + "from Booking as b "
            + "join  b.item as i "
            + "join  b.booker as booker"
            + " where booker.id = :userId and i.id = :itemId and b.status = 'APPROVED' and  b.end < current_timestamp")
    List<Booking> findBookingByBookerIdAndItemIdAndStatusApproved(@Param("userId") Long bookerId,
                                                                  @Param("itemId") Long itemId);

    @Query(value = "select b from Booking b where b.item.user.id = ?1")
    Optional<List<Booking>> findAllByOwnerId(Long userId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.user.id = ?1 and b.start < ?2 and b.end > ?2")
    Optional<List<Booking>> findAllByOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime localDateTime,
                                                                      Pageable pageable);

    @Query(value = "select b from Booking b where b.item.user.id = ?1 and b.end < ?2")
    Optional<List<Booking>> findAllByOwnerIdAndEndBefore(Long userId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.user.id = ?1 and b.status = ?2")
    Optional<List<Booking>> findAllByOwnerIdAndStatus(Long userId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.user.id = ?1 and b.start > ?2")
    Optional<List<Booking>> findAllByOwnerIdAndStartAfter(Long userId, LocalDateTime localDateTime, Pageable pageable);
}
