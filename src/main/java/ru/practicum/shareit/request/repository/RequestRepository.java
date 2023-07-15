package ru.practicum.shareit.request.repository;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.models.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, PagingAndSortingRepository<Request, Long> {
    Long countAllByRequesterId(Long requesterId);

    List<Request> findAllByRequesterIdOrderByCreatedAsc(Long requesterId, Pageable pageable);

    List<Request> findAllByRequesterIdNot(Long userID, Pageable pageable);

    boolean existsById(@NonNull Long requestId);
}