package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.ewm.dto.ViewStatsDto(hit.app, hit.uri, COUNT(hit.uri)) " +
            "FROM EndpointHit AS hit WHERE hit.moment BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.app, hit.uri ORDER BY COUNT(hit.uri) DESC")
    List<ViewStatsDto> findAllInDateRange(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.ewm.dto.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit AS hit WHERE hit.moment BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.app, hit.uri ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<ViewStatsDto> findAllInDateRangeWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.dto.ViewStatsDto(hit.app, hit.uri, COUNT(hit.uri)) " +
            "FROM EndpointHit AS hit WHERE (hit.moment BETWEEN ?1 AND ?2) AND hit.uri IN (?3) " +
            "GROUP BY hit.app, hit.uri ORDER BY COUNT(hit.uri) DESC")
    List<ViewStatsDto> findAllInUriRange(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.ewm.dto.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit AS hit WHERE (hit.moment BETWEEN ?1 AND ?2) AND hit.uri IN (?3) " +
            "GROUP BY hit.app, hit.uri, hit.ip ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<ViewStatsDto> findAllInUriRangeWithUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}
