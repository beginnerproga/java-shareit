package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerOrderByStartDesc(User user);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime start);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime end);

    List<Booking> findByBookerAndItemAndEndBeforeOrderByStartDesc(User user, Item item, LocalDateTime end);

    List<Booking> findByItem_OwnerOrderByStartDesc(User user);

    List<Booking> findByItem_OwnerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_OwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime start);

    List<Booking> findByItem_OwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime end);

    Booking findTop1ByItemAndItem_OwnerAndEndBeforeAndStatusOrderByStartDesc(Item item, User user, LocalDateTime end, Status status);

    Booking findTop1ByItemAndItem_OwnerAndStartAfterAndStatusOrderByStartAsc(Item item, User user, LocalDateTime start, Status status);
}