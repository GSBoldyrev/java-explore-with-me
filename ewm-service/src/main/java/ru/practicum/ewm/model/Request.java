package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.misc.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime created;
    @Column(name = "status")
    private RequestStatus status;
    @ManyToOne
    @JoinColumn(name = "requester")
    private User requester;
    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;
}
