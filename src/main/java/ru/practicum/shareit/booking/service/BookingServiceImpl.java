package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long userId) {
        log.info("Received request to add booking from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new ItemNotFoundException("item with id=" + bookingDto.getItemId() + " not found");
        });
        if (!item.getAvailable())
            throw new ItemIsNotAvailableException("Item with id = " + item.getId() + " is not available for booking");
        if (item.getOwner().getId() == userId)
            throw new OwnerCantBookingItemException("Owner can't book his item.");
        Booking booking = BookingMapper.toBooking(bookingDto, item, user, Status.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingInfoDto acceptBooking(Long bookingId, boolean approved, Long userId) {
        log.info("Received request to change status of  booking with booking's id = " + bookingId + " from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Booking with id=" + bookingId + " not found");
        });
        if (booking.getItem().getOwner().getId() != user.getId())
            throw new UserNotHaveAccessException("User with id = " + userId + " doesn't have access to change status of booking");
        if (!booking.getStatus().equals(Status.WAITING))
            throw new StatusAlreadySetException("Status already set for this booking");
        if (approved)
            booking.setStatus(Status.APPROVED);
        else
            booking.setStatus(Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto getBooking(Long bookingId, Long userId) {
        log.info("Received request to get booking with booking's id = " + bookingId + " from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Booking with id=" + bookingId + " not found");
        });
        if (booking.getItem().getOwner().getId() != user.getId() && booking.getBooker().getId() != user.getId())
            throw new UserNotHaveAccessException("User with id = " + userId + " doesn't have access to change status of booking");
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public List<BookingInfoDto> getBookingsForBooker(Long userId, String state) {
        log.info("Received request to get all bookings from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findByBookerOrderByStartDesc(user);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state);
                bookingList = bookingRepository.findByBookerAndStatusOrderByStartDesc(user, status);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case "PAST":
                bookingList = bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
            default:
                throw new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(BookingMapper::toBookingInfoDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingInfoDto> getBookingsForOwner(Long userId, String state) {
        log.info("Received request to get all bookings from user with user's id={}", userId);
        if (userId == null)
            throw new UserIdWasNotTransferredException("User's id is null");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User with id=" + userId + " not found");
        });
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findByItem_OwnerOrderByStartDesc(user);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state);
                bookingList = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(user, status);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findByItem_OwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case "PAST":
                bookingList = bookingRepository.findByItem_OwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
            default:
                throw new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(BookingMapper::toBookingInfoDto).collect(Collectors.toList());
    }

}
