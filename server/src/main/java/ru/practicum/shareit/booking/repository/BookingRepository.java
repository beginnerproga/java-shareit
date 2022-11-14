package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerOrderByStartDesc(User user, Pageable pageable);

    Page<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status status, Pageable pageable);

    Page<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerAndItemAndEndBeforeOrderByStartDesc(User user, Item item, LocalDateTime end);

    Page<Booking> findByItem_OwnerOrderByStartDesc(User user, Pageable pageable);

    Page<Booking> findByItem_OwnerAndStatusOrderByStartDesc(User user, Status status, Pageable pageable);

    Page<Booking> findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItem_OwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItem_OwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime end, Pageable pageable);

    Booking findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(Item item, User user, LocalDateTime end, Status status);

    Booking findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(Item item, User user, LocalDateTime start, Status status);
}