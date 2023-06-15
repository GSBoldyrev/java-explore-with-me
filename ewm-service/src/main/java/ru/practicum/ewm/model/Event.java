package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.misc.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @Column(name = "description")
    private String description;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime created;
    @Column(name = "published_on")
    private LocalDateTime published;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 100, nullable = false)
    private EventState state;
    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;
    @Embedded
    private Location location;
}
