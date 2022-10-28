package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByUserOrderByCreatedDesc(User user);

    @Query(value = "SELECT * FROM requests WHERE requestor_id != ?1 ORDER BY created DESC", nativeQuery = true)
    Page<ItemRequest> findOrderByCreatedDesc(long requestorId, Pageable pageable);

}
