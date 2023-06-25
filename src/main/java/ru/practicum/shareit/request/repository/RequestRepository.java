package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.models.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterIdNot(Long userId, Pageable sortedByCreated);

    List<Request> findAllByRequesterIdOrderByCreatedAsc(Long requesterId, Pageable sortedByCreated);
}
