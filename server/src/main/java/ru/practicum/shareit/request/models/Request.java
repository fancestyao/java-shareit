package ru.practicum.shareit.request.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false)
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
}