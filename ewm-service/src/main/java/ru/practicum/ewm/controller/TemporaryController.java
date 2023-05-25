package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.EndPointHitClient;
import ru.practicum.ewm.ViewStatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TemporaryController {

    private final EndPointHitClient hitClient;
    private final ViewStatsClient statsClient;

    private final RestTemplateBuilder builder = new RestTemplateBuilder();



    @PostMapping(path = "/hit")
    public ResponseEntity<Object> addHit(@RequestBody EndpointHitDto hit) {
        log.info("Посещение сервиса {} пользователем {}",
                hit.getApp(),
                hit.getIp());
        hitClient.setRest(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080/hit"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());

        return hitClient.addHit(hit);
    }

    @GetMapping(path = "stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) String[] uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {

        statsClient.setRest(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080/stats"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
        if (uris == null) {
            return statsClient.getNotSpecifiedStats(start, end, unique);
        } else {
            return statsClient.getSpecifiedStats(start, end, uris, unique);
        }

    }
}