package ru.practicum.shareit.user.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.user.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsById(@NonNull Long userId);
}
