package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.service.StatsMapper.fromHitDto;
import static ru.practicum.ewm.service.StatsMapper.toHitDto;

@Service
@AllArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public HitDto save(HitDto hitDto) {
        Hit hit = fromHitDto(hitDto);

        return toHitDto(statsRepository.save(hit));
    }

    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(LocalDateTime.now().plusSeconds(5))) {
            throw new BadRequestException("НАЧАЛЬНАЯ ДАТА НЕ МОЖЕТ БЫТЬ В БУДУЩЕМ");
        }
        if (end.isBefore(LocalDateTime.now().minusSeconds(5))) {
            throw new BadRequestException("КОНЕЧНАЯ ДАТА НЕ МОЖЕТ БЫТЬ В ПРОШЛОМ");
        }
        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findAllUniqueStatsByUris(start, end, uris);
            }
            return statsRepository.findAllUniqueStats(start, end);
        } else {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findAllStatsByUris(start, end, uris);
            }
            return statsRepository.findAllStats(start, end);
        }
    }
}
