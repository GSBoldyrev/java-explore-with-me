package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.util.List;

import static ru.practicum.ewm.service.EndpointHitMapper.fromString;

@Service
@RequiredArgsConstructor
public class ViewStatsService {

    private final EndpointHitRepository repository;

    public List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean unique) {

        if (!unique) {
            if (uris == null) {
                return repository.findAllInDateRange(fromString(start), fromString(end));
            } else {
                return repository.findAllInUriRange(fromString(start), fromString(end), uris);
            }
        } else {
            if (uris == null) {
                return repository.findAllInDateRangeWithUniqueIp(fromString(start), fromString(end));
            } else {
                return repository.findAllInUriRangeWithUniqueIp(fromString(start), fromString(end), uris);
            }
        }
    }
}
