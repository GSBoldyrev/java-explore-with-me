package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Component
@Slf4j
public class StatsClient {
    private final String local = "http://localhost:9090";
    private final RestTemplate rest = new RestTemplate();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(HitDto hitDto) {
        log.info("СОХРАНЯЕМ ВЫЗОВ ЭНДПОИНТА {}", hitDto.getUri());

        rest.postForLocation(local + "/hit", hitDto);
    }

    public List<StatsDto> get(LocalDateTime from, LocalDateTime to,
                              List<String> uris, boolean unique) {
        String start = "";
        String end = "";

        if (from != null && to != null) {
            start = from.format(dateTimeFormatter);
            end = to.format(dateTimeFormatter);
        }

        log.info("ЗАПРОС СТАТИСТИКИ ПО ЭНДПОИНТАМ {}, C {} ПО {}", uris.toString(), start, end);

        ResponseEntity<StatsDto[]> stats = rest.getForEntity(local + "/stats?start=" + start +
                "&end=" + end + "&uris=" + uris + "&unique=" + unique, StatsDto[].class);

        return Arrays.asList(Objects.requireNonNull(stats.getBody()));
    }
}