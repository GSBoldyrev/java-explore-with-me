package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventInitiatorIdAndEventId(Long userId, Long eventId);

    boolean existsByRequester(User user);

    @Query("SELECT r " +
            "FROM Request AS r " +
            "WHERE r.requester.id = :requester")
    List<Request> findAllByRequester(@Param("requester") Long requester);

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.event.initiator.id = :user AND r.event.id = :event AND r.id IN :requests " +
            "ORDER BY r.created")
    List<Request> findAllByParam(@Param("user") Long userId,
                                 @Param("event") Long eventId,
                                 @Param("requests") List<Long> requests);
}
