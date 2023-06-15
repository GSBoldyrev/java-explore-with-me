package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto save(@RequestBody HitDto hit) {
        log.info("СОХРАНЯЕМ ВЫЗОВ ЭНДПОИНТА {}", hit.getUri());

        return service.save(hit);
    }

    @GetMapping("/stats")
    public List<StatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                              @RequestParam(required = false) List<String> uris,
                              @RequestParam(defaultValue = "false") boolean unique) {
        log.info("ЗАПРОС СТАТИСТИКИ В СЕРВЕРЕ ПО ЭНДПОИНТАМ {} С {} ПО {}", uris, start, end);
        List<StatsDto> stats = service.get(start, end, uris, unique);
        log.info("ПРАВКА ТЕСТОВ. СЕРВЕР ОТПРАВЛЯЕТ СТАТИСТИКУ {}", stats.size());

        return stats;
    }
}
