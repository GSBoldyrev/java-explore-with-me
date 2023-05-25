package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndPointHitClient;
import ru.practicum.ewm.ViewStatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TemporaryController {

    private final EndPointHitClient hitClient;
    private final ViewStatsClient statsClient;

    @PostMapping(path = "/hit")
    public ResponseEntity<Object> addHit(@RequestBody EndpointHitDto hit) {
        log.info("Посещение сервиса {} пользователем {}",
                hit.getApp(),
                hit.getIp());

        return hitClient.addHit(hit);
    }

    @GetMapping(path = "stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) String[] uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        if (uris == null) {
            return statsClient.getNotSpecifiedStats(start, end, unique);
        } else {
            return statsClient.getSpecifiedStats(start, end, uris, unique);
        }

    }
}