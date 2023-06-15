package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.misc.EventState;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(long catId);

    List<Event> findAllByIdIn(Set<Long> ids);

    @Query("SELECT e FROM Event AS e WHERE e.initiator.id = :initiator ORDER BY e.created DESC")
    List<Event> findAllForInitiator(@Param("initiator") long initiator, Pageable page);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.initiator AS u " +
            "JOIN e.category AS c " +
            "WHERE (:users IS NULL OR u.id IN :users) " +
            "AND (:eventStates IS NULL OR e.state IN :eventStates)" +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (CAST(:eventStart AS timestamp) IS NULL OR e.eventDate >= :eventStart) " +
            "AND (CAST(:eventEnd AS timestamp) IS NULL OR e.eventDate <= :eventEnd)")
    Page<Event> findAllForAdminApi(@Param("users") List<Long> users,
                                   @Param("eventStates") List<EventState> eventStates,
                                   @Param("categories") List<Long> categories,
                                   @Param("eventStart") LocalDateTime eventStart,
                                   @Param("eventEnd") LocalDateTime eventEnd,
                                   Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "JOIN e.initiator AS u " +
            "JOIN e.category AS c " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL " +
            "OR (UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')))) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:onlyAvailable IS FALSE OR e.confirmedRequests < e.participantLimit) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            "ORDER BY e.eventDate ASC")
    Page<Event> findAllForPublicApi(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    @Param("onlyAvailable") Boolean onlyAvailable,
                                    Pageable pageable);
}
